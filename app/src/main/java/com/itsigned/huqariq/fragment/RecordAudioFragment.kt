package com.itsigned.huqariq.fragment

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.devlomi.record_view.OnRecordListener
import com.itsigned.huqariq.R
import com.itsigned.huqariq.bean.User
import com.itsigned.huqariq.database.DataBaseService
import com.itsigned.huqariq.helper.PermissionHelper
import com.itsigned.huqariq.helper.REQUEST_PERMISION_AUDIO
import com.itsigned.huqariq.helper.SystemFileHelper
import com.itsigned.huqariq.player.MediaPlayerHolder
import com.itsigned.huqariq.player.MediaRecordHolder
import com.itsigned.huqariq.serviceclient.RafiServiceWrapper
import com.itsigned.huqariq.util.RecordConstants.Companion.EXTENSION_WAV
import com.itsigned.huqariq.util.RecordConstants.Companion.FOLDER_AUDIO_DOWNLOAD
import com.itsigned.huqariq.util.RecordConstants.Companion.PREFIX_FILE_AUDIO_DOWNLOAD
import com.itsigned.huqariq.util.RecordConstants.Companion.URL_CHANCA
import com.itsigned.huqariq.util.Util
import com.itsigned.huqariq.util.session.SessionManager
import kotlinx.android.synthetic.main.fragment_record_audio.*
import kotlinx.android.synthetic.main.fragment_record_audio.view.*
import kotlinx.android.synthetic.main.fragment_record_audio.view.audioRecord
import kotlinx.android.synthetic.main.fragment_record_audio.view.seekbarAudioRecord
import okhttp3.RequestBody
import java.io.File


class RecordAudioFragment : Fragment(), MediaPlayerHolder.EventMediaPlayer ,MediaRecordHolder.EventMediaRecordHolder{

    private var audioFile:File? = null
    private var mPlayerAdapter: MediaPlayerHolder? = null
    private var mediaPlayerHolderForRecord: MediaPlayerHolder? = null
    private var mediaRecordHolder: MediaRecordHolder? = null
    private var mUserIsSeeking = false
    private var mUserRecordIsSeeking = false
    private lateinit var seekbarExample:SeekBar
    private lateinit var seekbarRecord:SeekBar
    private var index=0
    private lateinit var fileName:String
    private var frameAnimation: AnimationDrawable? = null
    lateinit var user: User


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_record_audio, container, false)
        user=SessionManager.getInstance(activity).userLogged
        fileName=Util.getFileName(user.codeDepartamento)
        initializeSeekbar(view)
        initializeSeekbarRecord(view)
        initializePlaybackController()
        PermissionHelper.recordAudioPermmision(context!!,this)
        index=user.avance
        initButton(view)
        view.audioRecord.visibility=View.GONE
        configureFunctionAudio(view)
        getAudioWebService()
        return view
    }

    private fun getAudioWebService(){
        val progress = Util.createProgressDialog(context, "Cargando")
        progress.show()
        val codeName=SystemFileHelper.getNameCodeFile(PREFIX_FILE_AUDIO_DOWNLOAD,EXTENSION_WAV)
        RafiServiceWrapper.downloadFile(context!!,URL_CHANCA, FOLDER_AUDIO_DOWNLOAD, codeName,
                {
                    mPlayerAdapter?.loadMediaFromPath(SystemFileHelper.getPathFile(FOLDER_AUDIO_DOWNLOAD,codeName))
                    deleteRecord()
                    progress.dismiss()
                },
                {error->
                    Toast.makeText(context!!,error,Toast.LENGTH_LONG).show()
                    progress.dismiss()
                }
        )
    }

    private fun configureFunctionAudio(view:View){
        view.record_view.setOnRecordListener(object: OnRecordListener {
            override fun onFinish(recordTime: Long) {finishRecordAudio()}
            override fun onLessThanSecond() {cancelRecordAudio()}
            override fun onCancel() {cancelRecordAudio() }
            override fun onStart() { recordAudio()}
        })
        view.startRecordButton.setRecordView(view.record_view)
        view.buttonSendAudio.setOnClickListener{ sendAudio() }
        view.buttonNextAudio.setOnClickListener { getAudioWebService() }
    }

    private fun finishRecordAudio(){
        mediaRecordHolder!!.stopRecord()
        startRecordButton.visibility=View.GONE
        buttonSendAudio.visibility=View.VISIBLE
    }

    private fun cancelRecordAudio(){
        mediaRecordHolder!!.cancelRecord()
        stopAnimation()
       // deleteRecord()
    }

    private fun initButton(view:View){
        view.ibPlay.setOnClickListener {
            showHiddenControlMedia(view.ibPlay,view.ibPause,true)
            mPlayerAdapter?.play()
        }
        view.ibPause.setOnClickListener {
            showHiddenControlMedia(view.ibPlay,view.ibPause,false)
            mPlayerAdapter?.pause()
        }
        view.ibPlayRecord.setOnClickListener {
            showHiddenControlMedia(view.ibPlayRecord,view.ibPauseRecord,true)
            mediaPlayerHolderForRecord?.play()
        }
        view.ibPauseRecord.setOnClickListener {
            showHiddenControlMedia(view.ibPlayRecord,view.ibPauseRecord,false)
            mediaPlayerHolderForRecord?.pause()
        }
        view.ivCloseRecord.setOnClickListener { this.dialogClose() }
    }

    private fun sendAudio(){
        val requestBody = RequestBody.create(null,audioFile!! )
        RafiServiceWrapper.uploadAudio(audioFile!!.name,requestBody,context!!,
                { getAudioWebService() }
                ,{ error-> Toast.makeText(context!!,error,Toast.LENGTH_LONG).show()
        })
    }

    private fun showHiddenControlMedia(viewPlay:View,viewPause:View,isPlaying: Boolean){
        viewPause.visibility = if (isPlaying) View.VISIBLE else View.GONE
        viewPlay.visibility = if (isPlaying) View.GONE else View.VISIBLE
    }


    private fun initializePlaybackController() {
        val mMediaPlayerHolder = MediaPlayerHolder(this.activity, this)
        mPlayerAdapter = mMediaPlayerHolder
        mediaRecordHolder=MediaRecordHolder(this)
        initMediaPlayerForRecord()
    }

    private fun initMediaPlayerForRecord(){
        mediaPlayerHolderForRecord= MediaPlayerHolder(this.activity!!, object : MediaPlayerHolder.EventMediaPlayer {
            override fun reinitAudio() { showHiddenControlMedia(ibPlayRecord,ibPauseRecord,false)}
            override fun changePositionSeek(pos: Int) { if (mUserRecordIsSeeking) return;seekbarRecord.progress = pos }
            override fun onPositionChanged(positon: Int) {
                if (!mUserRecordIsSeeking) seekbarRecord.progress = positon
                showHiddenControlMedia(ibPlayRecord,ibPauseRecord,false)
            }
            override fun onDurationChanged(duration: Int) { seekbarAudioRecord.max = duration }
        }
        )
    }


    private fun initializeSeekbar(view:View) {
        seekbarExample=view.seekbarAudioExample
        view.seekbarAudioExample.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    var userSelectedPosition = 0

                    override fun onStartTrackingTouch(seekBar: SeekBar) { mUserIsSeeking = true }

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        if (fromUser) userSelectedPosition = progress
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        mUserIsSeeking = false
                        mPlayerAdapter?.seekTo(userSelectedPosition)
                    }
                })
    }


    private fun initializeSeekbarRecord(view:View) {
        seekbarRecord=view.seekbarAudioRecord
        view.seekbarAudioRecord.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    var userSelectedPosition = 0

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        mUserRecordIsSeeking = true
                    }

                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        if (fromUser) userSelectedPosition = progress
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        mUserRecordIsSeeking = false
                        mediaPlayerHolderForRecord?.seekTo(userSelectedPosition)
                    }
                })
    }





    override fun onPositionChanged(positon: Int) {
        if (!mUserIsSeeking) seekbarExample.progress = positon
        if (view!=null)showHiddenControlMedia(view!!.ibPlayRecord,view!!.ibPauseRecord,false)
    }

    override fun onDurationChanged(duration: Int) { seekbarExample.max = duration }

    fun recordAudio(){
        val permisionRecordAudio=PermissionHelper.recordAudioPermmision(context!!,null)
        if(!permisionRecordAudio) return
        initAnimation()
        mediaRecordHolder?.initRecord(SessionManager.getInstance(activity).userLogged.dni,index,context!!)
    }

    private fun dialogClose(){

        val builder1 =  AlertDialog.Builder(this.context!!)
        builder1.setMessage("¿Desea eliminar la grabación?")
        builder1.setCancelable(true)
        builder1.setPositiveButton("Yes") { _, _ ->deleteRecord()}
        builder1.setNegativeButton("No") { dialog, _ ->dialog.cancel()}
        val alert11 = builder1.create()
        alert11.show()

    }

    private fun deleteRecord(){
        buttonSendAudio.visibility=View.GONE
        audioRecord.visibility=View.GONE
        startRecordButton.visibility=View.VISIBLE
        buttonSendAudio.visibility=View.GONE
        val dataBaseService=DataBaseService.getInstance(activity)
        dataBaseService.deleteAudio(user.email,index)


    }


    override fun finishRecord(pathAudio: String) {
        stopAnimation()
        audioRecord.visibility=View.VISIBLE
        val fileName=pathAudio.replace(Environment.getExternalStorageDirectory().absolutePath + "/game_catolic_quechua/","")
        audioFile = File(pathAudio)
        Util.copyFileUsingStream(audioFile,context)
        mediaPlayerHolderForRecord?.loadMediaFromPath(pathAudio)
        val dataBaseService=DataBaseService.getInstance(activity)
        dataBaseService.insertAudio(user.email,index,fileName)


    }


    private fun initAnimation() {
        audioRecord.visibility=View.GONE
        cardAnimation.visibility=View.VISIBLE
        ivAnimation.visibility = View.VISIBLE
        ivAnimation.setBackgroundResource(R.drawable.animsound)
        frameAnimation = ivAnimation.background as AnimationDrawable
        frameAnimation?.start()
    }

    private fun stopAnimation() {
        cardAnimation.visibility=View.GONE
        frameAnimation?.stop()
        ivAnimation.visibility = View.GONE
    }

    override fun reinitAudio() { showHiddenControlMedia( ibPlay,ibPause,false) }

    override fun changePositionSeek(pos: Int) {
        if (mUserIsSeeking) return
        seekbarExample.progress = pos
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
      Log.d("request permission","ddsds")
        when(requestCode){
            REQUEST_PERMISION_AUDIO->getAudioWebService()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

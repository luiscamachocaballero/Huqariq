package com.itsigned.huqariq.fragment

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.devlomi.record_view.OnRecordListener
import com.itsigned.huqariq.R
import com.itsigned.huqariq.bean.User
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
    private var frameAnimation: AnimationDrawable? = null
    private lateinit var user: User

    /**
     * Sobreescritura del metodo onCreateView
     * @param inflater clase para inflar el layout
     * @param container contenedor de la vista
     * @param savedInstanceState bundle con información del activity previo
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_record_audio, container, false)
        user=SessionManager.getInstance(activity).userLogged
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

    /**
     * Metodo que descarga el audio de muestra desde un WebService
     */
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


    /**
     * Metodo para la configuración inicial de  la vista view Record
     * @param view vista del layout actual
     */
    private fun configureFunctionAudio(view:View){
        view.record_view.setSoundEnabled(false);

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


    /**
     * Metodo para detener la grabacíón del audio del usuario
     */
    private fun finishRecordAudio(){
        mediaRecordHolder!!.stopRecord()
        startRecordButton.visibility=View.GONE
        buttonSendAudio.visibility=View.VISIBLE
    }

    /**
     * Metodo para cancelar la grabación del audio del Holder
     */
    private fun cancelRecordAudio(){
        mediaRecordHolder!!.cancelRecord()
        stopAnimation()
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

    /**
     * Metodo que invoca un servicio web para enviar el audio grabado por el usuario
     */
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


    /**
     * Metodo para inicializar los Seekbar de reproduccion de audio de muestra
     * @param view view con información del layout
     */
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

    /**
     * Metodo para inicializar los Seekbar de reproduccion de audio de grabación
     * @param view view con información del layout
     */
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


    /**
     * Metodo que se activa cada vez que se cambia la posicion del SeekBar
     * @param positon nueva posición del seekbar
     */
    override fun onPositionChanged(positon: Int) {
        if (!mUserIsSeeking) seekbarExample.progress = positon
        if (view!=null)showHiddenControlMedia(view!!.ibPlayRecord,view!!.ibPauseRecord,false)
    }


    override fun onDurationChanged(duration: Int) { seekbarExample.max = duration }

    /**
     * Metodo para iniciar la grabación de un adio
     */
    fun recordAudio(){
        val permisionRecordAudio=PermissionHelper.recordAudioPermmision(context!!,this)
        if(!permisionRecordAudio){
            record_view.cancelLongPress()
            return
        }
        initAnimation()
        mediaRecordHolder?.initRecord(SessionManager.getInstance(activity).userLogged.dni,index,context!!)
    }

    /**
     * Metodo para mostrar un cuadro de confirmación para eliminar el audio grabado
     */
    private fun dialogClose(){
        val builder1 =  AlertDialog.Builder(this.context!!)
        builder1.setMessage("¿Desea eliminar la grabación?")
        builder1.setCancelable(true)
        builder1.setPositiveButton("Yes") { _, _ ->deleteRecord()}
        builder1.setNegativeButton("No") { dialog, _ ->dialog.cancel()}
        val alert11 = builder1.create()
        alert11.show()
    }

    /**
     * Metodo para reiniciar la vista de grabación luego de eliminar el audio grabado
     */
    private fun deleteRecord(){
        buttonSendAudio.visibility=View.GONE
        audioRecord.visibility=View.GONE
        startRecordButton.visibility=View.VISIBLE
        buttonSendAudio.visibility=View.GONE
    }


    /**
     * Metodo para guardar la grabación de audio en un archivo
     * @param nameAudio ruta del archivo donde se va a guardar la grabación
     */
    override fun finishRecord(nameAudio: String) {
        stopAnimation()
        audioRecord.visibility=View.VISIBLE
        audioFile = File(nameAudio)
        Util.copyFileUsingStream(audioFile,context)
        mediaPlayerHolderForRecord?.loadMediaFromPath(nameAudio)
    }


    /**
     * Metodo para iniciar la animación de audio
     */
    private fun initAnimation() {
        audioRecord.visibility=View.GONE
        cardAnimation.visibility=View.VISIBLE
        ivAnimation.visibility = View.VISIBLE
        ivAnimation.setBackgroundResource(R.drawable.animsound)
        frameAnimation = ivAnimation.background as AnimationDrawable
        frameAnimation?.start()
    }

    /**
     * Metodo para ocultar la animación de audio
     */
    private fun stopAnimation() {
        cardAnimation.visibility=View.GONE
        frameAnimation?.stop()
        ivAnimation.visibility = View.GONE
    }

    /**
     * Metodo para reiniciar el controlador de audio
     */
    override fun reinitAudio() { showHiddenControlMedia( ibPlay,ibPause,false) }


    /**
     * Metodo para mover la posicion de la barra Seek
     * @param pos posiciion de la barra seek a donde se va a mover [ 0 - 100 ]
     */
    override fun changePositionSeek(pos: Int) {
        if (mUserIsSeeking) return
        seekbarExample.progress = pos
    }


    /**
     * Sobreescritura del metodo onRequestPermissionsResult para evaluar los resultados de las solicitudes de permisos
     * @param requestCode codigo de solicitud de permisos
     * @param permissions codigo de respuesta de solicitud de permisos
     * @param grantResults resultados individuales de los permisos
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_PERMISION_AUDIO->{
                getAudioWebService()

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context,R.string.message_need_permission, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,R.string.message_acept_permission, Toast.LENGTH_LONG).show()

                }


            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

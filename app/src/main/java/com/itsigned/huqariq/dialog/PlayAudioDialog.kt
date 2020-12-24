package com.itsigned.huqariq.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.itsigned.huqariq.R
import kotlinx.android.synthetic.main.record_audio_dialog.*


class PlayAudioDialog(val idRaw:Int,val message:String) : DialogFragment() {
    private  var mediaPlayer: MediaPlayer? = null
    private var initRecord=false




    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.record_audio_dialog, null, false)
    }



    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view:View, @Nullable  savedInstanceState:Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCancelable(false)
        textViewMessageAudio.setText(message)
        buttonCancel.setOnClickListener{
            if(initRecord)mediaPlayer!!.stop();dismiss()}
        buttonMicStart.setOnClickListener{playAudio()}
        buttonMicStop.setOnClickListener{stopButton()}

    }

    override fun onCreateDialog( savedInstanceState:Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun playAudio(){

        mediaPlayer = MediaPlayer.create(context!!, idRaw)
        mediaPlayer!!.setOnCompletionListener { reinitView() }
        mediaPlayer!!.start()
        waveFormView.updateAmplitude(1f,true)
        initRecord=true
        buttonMicStart.hide()
        buttonMicStop.show()

    }


    private fun stopButton() {
        mediaPlayer!!.stop()
        reinitView()

    }

    private fun reinitView(){
        if(!isVisible)return
        buttonMicStart.show()
        buttonMicStop.hide()
        waveFormView.updateAmplitude(0f,false)
        initRecord=false

    }






}

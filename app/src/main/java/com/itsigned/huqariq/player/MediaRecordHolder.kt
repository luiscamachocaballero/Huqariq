package com.itsigned.huqariq.player

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import com.itsigned.huqariq.util.Util
import java.io.File
import java.io.File.separator


class MediaRecordHolder (event: EventMediaRecordHolder){



    private var mediaRecorder: MediaRecorder? = null
    var lastAudioRecord: String? = null
    private var eventMediaRecordHolder: EventMediaRecordHolder=event




    private fun getMediaRecorderReady(dni:String,index: Int,context: Context): MediaRecorder {
        createDirectory()
        val mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder.setAudioChannels(1)
        mediaRecorder.setAudioSamplingRate(16000)
        lastAudioRecord = getPathToAudio(dni,index,context)
        Log.d("MediarecordHolder","last audio record "+lastAudioRecord)
        mediaRecorder.setOutputFile(lastAudioRecord)
        return mediaRecorder
    }


    private fun createDirectory() {
        val folder = File(getExternalStorageDirectory().absolutePath + separator + "game_catolic_quechua")
        if (!folder.exists()) {
            folder.mkdirs()
        }

    }

    private fun getPathToAudio(dni:String,index:Int,context: Context): String {
        return getExternalStorageDirectory().absolutePath + "/game_catolic_quechua/" + getCode(dni,index,context)
    }

    private fun getPathToAudioOld(dni:String,idAudio:Int,context: Context): String {
        return getExternalStorageDirectory().absolutePath + "/game_catolic_quechua/" + getCode(dni,idAudio,context)
    }


    private fun getCode(dni:String,idAudio:Int,context:Context): String {
        val dateText= Util.getStringDate()
        var code = "${dni}_${dateText}_${idAudio+2}.wav"
        return code
    }


    fun stopRecord(){
        mediaRecorder?.stop()
        Log.d("MediaRecord holder", "finish record in "+lastAudioRecord)
        eventMediaRecordHolder.finishRecord(lastAudioRecord!!)
    }

    fun cancelRecord(){
        try {
            mediaRecorder?.stop()
        }catch (e:java.lang.Exception){
        }
    }


    fun initRecord(dni:String,index:Int,context: Context) {
        Log.d("MediaRecordHolder","Init record")
            mediaRecorder = getMediaRecorderReady(dni,index,context)
            try {
                mediaRecorder?.prepare()
                mediaRecorder?.start()
            } catch (e: Exception) {
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
                e.printStackTrace()
            }
    }


    interface EventMediaRecordHolder {
        fun finishRecord(nameAudio:String)

    }



}
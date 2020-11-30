package com.itsigned.huqariq.player

import android.content.Context
import android.media.MediaPlayer
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit




class MediaPlayerHolder(context: FragmentActivity?, event:EventMediaPlayer) {

    private var mContext: Context? = context
    private lateinit var mMediaPlayer: MediaPlayer
    private var resourceFileAsset: String = ""
    private  var mExecutor: ScheduledExecutorService?=null
    private var mSeekbarPositionUpdateTask: Runnable? = null
    private   var eventMediaPlayer:EventMediaPlayer=event
    private val PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 300L





    private fun initializeMediaPlayer() {
        //if (::mMediaPlayer.isInitialized) return
        mMediaPlayer = MediaPlayer()
        mMediaPlayer.setVolume(100f,100f)
        mMediaPlayer.setOnCompletionListener { stopUpdatingCallbackWithPosition(true) }
    }

    private fun stopUpdatingCallbackWithPosition(resetUIPlaybackPosition: Boolean) {
        if (mExecutor == null) return
        mExecutor?.shutdownNow()
        mExecutor = null
        mSeekbarPositionUpdateTask = null
        if (resetUIPlaybackPosition){
            eventMediaPlayer.onPositionChanged(0)

            eventMediaPlayer.reinitAudio()
        }




    }

    fun play() {
        if (::mMediaPlayer.isInitialized && !mMediaPlayer.isPlaying) {
            mMediaPlayer.start()
            startUpdatingCallbackWithPosition()
        }
    }


    fun pause() {
        if (::mMediaPlayer.isInitialized && mMediaPlayer.isPlaying) mMediaPlayer.pause()
    }


    private fun startUpdatingCallbackWithPosition() {
        if (mExecutor == null) mExecutor = Executors.newSingleThreadScheduledExecutor()
        if (mSeekbarPositionUpdateTask == null) mSeekbarPositionUpdateTask = Runnable { updateProgressCallbackTask() }
        mExecutor?.scheduleAtFixedRate(mSeekbarPositionUpdateTask, 0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS)
    }


    private fun updateProgressCallbackTask() {
        if (!mMediaPlayer.isPlaying) return
        val currentPosition = mMediaPlayer.currentPosition
        eventMediaPlayer.changePositionSeek(currentPosition)

    }


    private fun loadMedia(fileNameAsset: String) {
        resourceFileAsset = fileNameAsset
        initializeMediaPlayer()
        try {
            val descriptor = mContext?.assets?.openFd(resourceFileAsset)
            mMediaPlayer.setDataSource(descriptor?.fileDescriptor, descriptor!!.startOffset, descriptor.length)
            descriptor.close()
            mMediaPlayer.prepare()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        initializeProgressCallback()
    }

    fun loadMediaFromPath(path: String) {
        resourceFileAsset = path
        initializeMediaPlayer()
        try {
            mMediaPlayer.setDataSource(path)
            mMediaPlayer.prepare()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        initializeProgressCallback()
    }


    private fun initializeProgressCallback() {
        val duration = mMediaPlayer.duration
        eventMediaPlayer.onDurationChanged(duration)
        eventMediaPlayer.onPositionChanged(0)

    }


    fun seekTo(position: Int) {
        if (!::mMediaPlayer.isInitialized)return
        mMediaPlayer.pause()
        mMediaPlayer.seekTo(position)
        mMediaPlayer.start()

    }

    fun reset(){
        mMediaPlayer.reset()
        loadMedia(resourceFileAsset)
        stopUpdatingCallbackWithPosition(true)

    }

    interface EventMediaPlayer{

        fun changePositionSeek(pos:Int)
        fun onPositionChanged(positon:Int)
        fun onDurationChanged( duration:Int)
        fun reinitAudio()
    }


}
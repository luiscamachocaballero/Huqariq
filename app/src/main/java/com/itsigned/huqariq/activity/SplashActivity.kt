package com.itsigned.huqariq.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.itsigned.huqariq.R
import com.itsigned.huqariq.helper.goToActivity
import com.itsigned.huqariq.util.session.SessionManager
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import java.util.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        AppCenter.start(application, "9080c742-2242-4b86-8e4f-c971edeb4158",
                Analytics::class.java, Crashes::class.java)

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                val isLogued=SessionManager.getInstance(this@SplashActivity).isLogged
                if (isLogued) goToActivity() else goToActivity(LoginActivity::class.java)
            }
        }
        val timer = Timer()
        timer.schedule(task, 4000)
    }
}
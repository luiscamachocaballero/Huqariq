package com.itsigned.huqariq.fragment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import com.itsigned.huqariq.R
import com.itsigned.huqariq.activity.GetFormDataStepperAction
import com.itsigned.huqariq.activity.MainActivity
import com.itsigned.huqariq.dialog.PlayAudioDialog
import com.itsigned.huqariq.util.Consentiment
import com.itsigned.huqariq.util.Util
import kotlinx.android.synthetic.main.fragment_step_four.*
import java.io.File
import java.util.*


class StepFourFragment : StepFragment() {

    var action: GetFormDataStepperAction?=null




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_step_four, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            confirmationText.text = Html.fromHtml(Consentiment.CONSENTIMENT_TEXT, Html.FROM_HTML_MODE_COMPACT)
        } else {
            confirmationText.text = Html.fromHtml(Consentiment.CONSENTIMENT_TEXT)
        }
        downloadButton.setOnClickListener {
            hackDisabled()
            val path=Util.saveConsentiment(context!!)
            sendNotification(path)
        }

        playButton.setOnClickListener {
            val dialog= PlayAudioDialog(R.raw.consentimientoaudio,"Presione play para escuchar el Consentimiento Informado")
            dialog.isCancelable=false
            dialog.show(activity!!.supportFragmentManager,"")
        }

    }




    private fun sendNotification(path: String) {
        Log.d("Send Notification","sendNotification")
        hackDisabled()
        val intent = Intent(context!!, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val pending = PendingIntent.getActivity(context!!,
                0, openFile(path),  PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(context!!)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.myc)
                .setAutoCancel(false)
                .setContentTitle("consentimiento.pdf")
                .setContentText("Descargado")
                .addAction(R.mipmap.ic_launcher, "Abrir",
                        pending)
        //  .setAutoCancel(true)
        //  .setSound(defaultSoundUri)
        //   .setContentIntent(pendingIntent);
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        val `as` = notificationBuilder.build()
        `as`.flags = `as`.flags or Notification.FLAG_NO_CLEAR
        notificationManager.notify(10000, notificationBuilder.build())
    }

    private fun openFile(path:String):Intent{
        val file = File(path)
        val uri =  Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_VIEW)
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase(
                Locale.getDefault()))
        intent.setDataAndType(uri, mimeType)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return  intent
    }



    override fun verifyStep() {
        aceptConsentimentCheckBox.error=null
        if(aceptConsentimentCheckBox.isChecked){
            this.action!!.goNextStep()
        }
        else{
            aceptConsentimentCheckBox.isFocusable=true
            aceptConsentimentCheckBox.requestFocus()
            aceptConsentimentCheckBox.setError("Debe Aceptar el Consentimiento para continuar")
        }

    }



    private fun hackDisabled() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
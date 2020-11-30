package com.itsigned.huqariq.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val REQUEST_PERMISION_AUDIO=3

class PermissionHelper {
    companion object{
        fun recordAudioPermmision(context: Context, fragment: Fragment?):Boolean{

            return check(context,fragment,arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISION_AUDIO )
        }

        private fun check(context:Context, fragment: Fragment?, listPermision: Array<String>, codePermision:Int):Boolean{
            var hasAllPermision=true
            listPermision.forEach { permise-> if(!hasPermission(context,permise)) hasAllPermision=false }
            if(hasAllPermision) return true
            if(fragment==null) ActivityCompat.requestPermissions(context as AppCompatActivity, listPermision, codePermision)
            else fragment.requestPermissions(listPermision,codePermision)
            return false
        }

        private fun hasPermission(context:Context,permission: String): Boolean{
            return  ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}
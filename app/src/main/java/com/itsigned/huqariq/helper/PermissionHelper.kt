package com.itsigned.huqariq.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

const val REQUEST_PERMISION_AUDIO=3
const val REQUEST_PERMISION_PLAY_AUDIO=2

class PermissionHelper {
    companion object{
        fun playAudioPermission(context: Context, fragment: Fragment?):Boolean{

            return check(context,fragment,arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISION_PLAY_AUDIO )
        }

        fun recordAudioPermmision(context: Context, fragment: Fragment?):Boolean{

            return check(context,fragment,arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISION_AUDIO )
        }

        /**
         * Metodo para verificar un listado de permisos
         * @param context contexto
         * @param fragment fragmento donde se ejecuta la peticion de permisos
         * @param listPermision lista de permisos a preguntar al usuario
         * @param codePermision codigo de peticion de los permisions
         * @return resultado de la peticion
         */
        private fun check(context:Context, fragment: Fragment?, listPermision: Array<String>, codePermision:Int):Boolean{
            var hasAllPermision=true
            listPermision.forEach { permise-> if(!hasPermission(context,permise)) hasAllPermision=false }
            if(hasAllPermision) return true
            Log.d("PERMISSION ","Has all permission "+hasAllPermision)
            if(fragment==null) ActivityCompat.requestPermissions(context as AppCompatActivity, listPermision, codePermision)
            else fragment.requestPermissions(listPermision,codePermision)
            return false
        }

        /**
         * Metodo para consultar si se tiene actvado un permiso en especifico
         * @param context contexto
         * @param permission permiso a consultar
         */
        private fun hasPermission(context:Context,permission: String): Boolean{
            return  ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}
package com.itsigned.huqariq.helper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.itsigned.huqariq.R
import com.itsigned.huqariq.activity.MainActivity
import com.itsigned.huqariq.util.Constants

fun AppCompatActivity.goToActivity(cl:Class<*> = MainActivity::class.java, finish:Boolean=true, clearAll:Boolean=false) {
    if(finish)this.finish()
    val mainIntent = Intent(this,cl )
    if(clearAll) mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(mainIntent)
}

fun FragmentActivity.getLoadingProgress(idLayout:Int=R.layout.dialog_custom_progress): Dialog {
    val customDialog = AlertDialog.Builder(this)
    val viewDialog =  layoutInflater.inflate(idLayout, null)
    customDialog.setView(viewDialog)
    customDialog.setCancelable(false)
    return customDialog.create()
}

fun Fragment.getLoadingProgress(idLayout:Int=R.layout.dialog_custom_progress): Dialog {
    val customDialog = AlertDialog.Builder(context)
    val viewDialog =  layoutInflater.inflate(idLayout, null)
    customDialog.setView(viewDialog)
    customDialog.setCancelable(false)
    return customDialog.create()
}

fun Fragment.setErrorEditText(editText: EditText, idString:Int){
    editText.error=getString(idString)
    editText.isFocusable=true
    editText.requestFocus()
}


fun AppCompatActivity.hasErrorEditTextEmpty(editText: EditText, idMessage:Int):Boolean{
    if(editText.text.toString().compareTo(Constants.VACIO)==0){
        showError(editText, getString(idMessage))
        return true
    }
    return false
}

/**
 * Metodo para mostrar un toast
 * @param message mensaje a mostrar en el toast
 */
fun AppCompatActivity.showMessage(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

/**
 * Metodo para mostrar un toast
 * @param message mensaje a mostrar en el toast
 */
fun Fragment.showMessage(message:String){
    Toast.makeText(context!!,message,Toast.LENGTH_LONG).show()
}

/**
 * Metodo para mostrar un error en un editText
 * @param editText edittext donde se mostrara el error
 * @param message mensaje de error a mostrar
 */
fun AppCompatActivity.showError(editText: EditText, message: String) {
    editText.error = null
    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    editText.error = message
    editText.isFocusable = true
    editText.requestFocus()
}

/**
 * Metodo que coloca un toolbar en un activity
 */


class ViewHelper {
    companion object{

        /**
         * Metodo para mostrar una vista y ocultar las restantes
         * @param viewToShow vista a mostrar
         * @param parent contenedor de vistas
         */
        fun showOneView(viewToShow: View, parent: View){
            for (index in 0 until (parent as ViewGroup).childCount) {
                val nextChild = parent.getChildAt(index)
                nextChild.visibility= View.GONE
            }
            viewToShow.visibility= View.VISIBLE
        }


        /**
         * Metodo para setear como no modificable un editText
         * @param editText editText a modificar
         * @param value valor a colocar en el editText
         */
        fun setEditTextNotModify(editText:EditText,value:String){
            editText.setText(value)
            editText.isEnabled=false
        }
    }
}
package com.itsigned.huqariq.helper

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.itsigned.huqariq.R
import com.itsigned.huqariq.activity.MainActivity
import com.itsigned.huqariq.util.Constants

fun AppCompatActivity.goToActivity(cl:Class<*> = MainActivity::class.java, finish:Boolean=true, clearAll:Boolean=false) {
    if(finish)this.finish()
    val mainIntent = Intent(this,cl )
    if(clearAll) mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(mainIntent)
}


fun AppCompatActivity.hasErrorEditTextEmpty(editText: EditText, idMessage:Int):Boolean{
    if(editText.text.toString().compareTo(Constants.VACIO)==0){
        showError(editText, getString(idMessage))
        return true
    }
    return false
}

fun AppCompatActivity.showMessage(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.showError(editText: EditText, message: String) {
    editText.error = null
    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    editText.error = message
    editText.isFocusable = true
    editText.requestFocus()
}

fun AppCompatActivity.setupToolbar() {
    val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
    setSupportActionBar(toolbar)
    val ab = supportActionBar
    ab!!.setDisplayHomeAsUpEnabled(true)
}



class ViewHelper {
    companion object{
        fun showOneView(viewToShow: View, parent: View){
            for (index in 0 until (parent as ViewGroup).childCount) {
                val nextChild = parent.getChildAt(index)
                nextChild.visibility= View.GONE
            }
            viewToShow.visibility= View.VISIBLE
        }

        fun showHide(viewToShow:View,viewToHide:View){
            viewToShow.visibility=View.VISIBLE
            viewToHide.visibility=View.GONE
        }

        fun setEditTextNotModify(editText:EditText,value:String){
            editText.setText(value)
            editText.isEnabled=false
        }
    }
}
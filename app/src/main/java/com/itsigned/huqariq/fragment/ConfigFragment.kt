package com.itsigned.huqariq.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.itsigned.huqariq.R
import com.itsigned.huqariq.bean.User
import com.itsigned.huqariq.database.DataBaseService
import com.itsigned.huqariq.util.Constants
import com.itsigned.huqariq.util.session.SessionManager
import kotlinx.android.synthetic.main.fragment_config.*
import kotlinx.android.synthetic.main.fragment_config.view.*
import kotlinx.android.synthetic.main.fragment_config.view.cbEnabled
import kotlinx.android.synthetic.main.fragment_config.view.etInstitution
import kotlinx.android.synthetic.main.fragment_config.view.etRegion


class ConfigFragment : Fragment() {


    private lateinit var user:User


    /**
     * Sobreescritura del metodo onCreateView
     * @param inflater clase para inflar el layout
     * @param container contenedor de la vista
     * @param savedInstanceState bundle con información del activity previo
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_config, container, false)
        user=DataBaseService.getInstance(activity).getUser(SessionManager.getInstance(activity).userLogged.email)
        view.etRegion.setText(user.region)
        view.etInstitution.setText(user.institution)
        view.cbEnabled.setOnCheckedChangeListener { _, b -> this.updateEditText(view,b) }
    view.btSaveCongiguration.setOnClickListener {saveCongiguration()}
    if (user.isMember==Constants.IS_MEMBER){view.cbEnabled.isChecked=true
        view.tvTextChek.text = "HABILITADO"
    }
    return view


}

/**
 * Metodo para guardar la configuración creada porel usuario
 */
    private fun saveCongiguration() {
        if (cbEnabled.isChecked){
            if (etRegion.text.toString()==""){
                showError(etRegion,"Dato inválido")
                return
            }
            if (etInstitution.text.toString()==""){
                showError(etInstitution,"Dato inválido")
                return
            }
            DataBaseService.getInstance(activity).editMember(etInstitution.text.toString(),etRegion.text.toString(),
                    Constants.IS_MEMBER,user.email)
            Toast.makeText(activity,"DATOS GUARDADOS",Toast.LENGTH_LONG).show()

        }else{
            DataBaseService.getInstance(activity).editMember(etInstitution.text.toString(),etRegion.text.toString(),
                    0,user.email)
            Toast.makeText(activity,"DATOS GUARDADOS",Toast.LENGTH_LONG).show()

        }

    }


    /**
     * Metodo para mostrar un error y un mensaje sobre un editText dado
     * @param editText editText donse se colocará el error
     * @param message mensaje de error a mostrar
     */
    private fun showError(editText: EditText, message:String){
        Toast.makeText(context, message , Toast.LENGTH_LONG).show()
        editText.error = message
        editText.isFocusable = true
        editText.requestFocus()
    }

    /**
     * Meotodo para actualizar la vista de configuración
     * @param view objeto con la información del layout
     * @param condition boleano para habilitar el textView
     */
    private fun updateEditText(view:View,condition:Boolean){
        if (condition) view.tvTextChek.text = "HABILITADO" else view.tvTextChek.setText("DESHABILITADO")
        view.etInstitution.isEnabled=condition
        view. etRegion.isEnabled=condition
    }







}

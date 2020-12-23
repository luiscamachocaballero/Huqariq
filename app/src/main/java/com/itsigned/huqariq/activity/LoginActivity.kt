package com.itsigned.huqariq.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.itsigned.huqariq.R
import com.itsigned.huqariq.helper.PermissionHelper
import com.itsigned.huqariq.helper.goToActivity
import com.itsigned.huqariq.helper.hasErrorEditTextEmpty
import com.itsigned.huqariq.helper.showError
import com.itsigned.huqariq.mapper.GeneralMapper
import com.itsigned.huqariq.model.LoginRequestDto
import com.itsigned.huqariq.serviceclient.RafiServiceWrapper
import com.itsigned.huqariq.util.Util
import com.itsigned.huqariq.util.session.SessionManager
import kotlinx.android.synthetic.main.activity_login.*

private const val REQUEST_SIGNUP = 0
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (SessionManager.getInstance(this).isLogged) goToActivity()
        configureActionButton()
        PermissionHelper.recordAudioPermmision(this,null)

    }

    /**
     * Metodo con las configuraciones iniciales de los botones
     */
    private fun configureActionButton() {
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
        }
        btnLogin.setOnClickListener { login() }
    }

    /**
     * Metodo para autentificar al usuario
     */
    private fun verifyLoginExtern() {
        val progress = Util.createProgressDialog(this, "Cargando")
        progress.show()
        RafiServiceWrapper.loginUser(this,
                LoginRequestDto(email = etEmail.text.toString(), password = etPass.text.toString()),
                { loginUser ->
                    progress.dismiss()
                    SessionManager.getInstance(baseContext).createUserSession(GeneralMapper.loginUserDtoDtoToUser(loginUser))
                    goToActivity()
                },
                { error ->
                    progress.dismiss()
                    Toast.makeText(baseContext, error, Toast.LENGTH_LONG).show()
                })

    }


    /**
     * Sobreescritura del metodo onActivityResult
     * @param requestCode codigo del Request del activity
     * @param resultCode codigo de resultado del activity
     * @param data extraData con información de la accion
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("result ", "result $resultCode request $requestCode")
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                this.finish()
            }
        }
    }

    /**
     * Metodo para validar los datos del login
     */
    private fun login() {
        if (hasErrorEditTextEmpty(etEmail, R.string.registro_message_error_ingrese_correo_electronico)) return
        if (hasErrorEditTextEmpty(etPass, R.string.registro_message_error_ingrese_contrasena_usuario)) return
        if (!Util.validarCorreo(etEmail.text.toString())) {
            showError(etEmail, getString(R.string.registro_message_error_correo_electronico_invalido))
            return
        }
        verifyLoginExtern()
    }


}

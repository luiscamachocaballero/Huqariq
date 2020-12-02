package com.itsigned.huqariq.activity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.itsigned.huqariq.R
import com.itsigned.huqariq.bean.Ubigeo
import com.itsigned.huqariq.bean.User
import com.itsigned.huqariq.database.DataBaseService
import com.itsigned.huqariq.util.Util
import androidx.appcompat.app.AppCompatActivity
import com.itsigned.huqariq.util.session.SessionManager
import kotlinx.android.synthetic.main.activity_register.*
import com.itsigned.huqariq.helper.*
import com.itsigned.huqariq.mapper.GeneralMapper
import com.itsigned.huqariq.model.*
import com.itsigned.huqariq.serviceclient.RafiServiceWrapper
import kotlinx.android.synthetic.main.activity_register.btnSignup
import kotlinx.android.synthetic.main.activity_register.etDni
import kotlinx.android.synthetic.main.activity_register.etEmail
import kotlinx.android.synthetic.main.activity_register.etName
import kotlinx.android.synthetic.main.activity_register.etPassword
import kotlinx.android.synthetic.main.activity_register.etPaterno
import kotlinx.android.synthetic.main.activity_register.etTelefono
import kotlinx.android.synthetic.main.activity_register.spDepartamento
import kotlinx.android.synthetic.main.activity_register.spDistrito
import kotlinx.android.synthetic.main.activity_register.spProvincia
import java.util.*
import kotlin.collections.ArrayList

private const val SEARCH_DEPARTMENT=1
private const val SEARCH_PROVINCIA=2
private const val SEARCH_DISTRITO=3

const val STATUS_MAIL_NOT_CALL_SERVICE=0
const val STATUS_MAIL_GREEN=1
const val STATUS_MAIL_RED=2
const val STATUS_MAIL_LOADING=3

const val STATUS_NUMBERDOCUMENT_NOT_CALL_SERVICE=0
const val STATUS_NUMBERDOCUMENT_GREEN=1
const val STATUS_NUMBERDOCUMENT_RED=2
const val STATUS_NUMBERDOCUMENT_LOADING=3


class RegisterActivity : AppCompatActivity() {

    private var listaDepartamento: ArrayList<Ubigeo>? = null
    private var departamento: String? = null
    private var provincia: String? = null
    private var distrito: String? = null
    private var ubigeoSelected:Ubigeo?=null
    private var idDialect:Int?=null
    private var statusMail: Int=0
    private var statusNumberDocument:Int=0
    private var listLanguage:ArrayList<Language>?=null


    /**
     * Metodo para la creación de activitys
     * @param savedInstanceState Bundle con información de la actividad previa
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupToolbar()
        val email=intent.getStringExtra("email")
        if(email != null)ViewHelper.setEditTextNotModify(etEmail,email)
        wrapperQueryDataBase(SEARCH_DEPARTMENT) { x->listaDepartamento=x }
        configureSpinner()
        etEmail.onFocusChangeListener= View.OnFocusChangeListener {
            _, hasFocus -> if(!hasFocus)validMail(etEmail.text.toString())
        }

        statusNumberDocument= STATUS_NUMBERDOCUMENT_NOT_CALL_SERVICE
        etDni.onFocusChangeListener= View.OnFocusChangeListener {
            _, hasFocus -> if(!hasFocus)validateNumberDocumentWithServer(etDni.text.toString())
        }
    }

    /**
     * Metodo que llama un webservice para validar el número de documento
     * @param numberDocument número de documento a validar
     */
    private fun validateNumberDocumentWithServer(numberDocument: String) {
        if (!ValidationHelper.validateIdentifyNumber(numberDocument)) {
            ViewHelper.showOneView(errorNumberDocumentImageView,validNumberDocumentStatusFrame)
            statusNumberDocument= STATUS_NUMBERDOCUMENT_RED
            showMessage(getString(R.string.view_form_register_step_two_document_incorrect_Format))
            return
        }
        statusNumberDocument= STATUS_NUMBERDOCUMENT_LOADING
        ViewHelper.showOneView(loadServiceNumberDocumentProgress,validNumberDocumentStatusFrame)
        RafiServiceWrapper.validateDni(this, RequestValidateDni(etDni.text.toString()),
                { success ->evaluateDni(success) },
                {
                    statusNumberDocument= STATUS_NUMBERDOCUMENT_NOT_CALL_SERVICE
                    showMessage(getString(R.string.default_error_server))
                }
        )
    }

    /**
     * Metodo para validar el correo electrónico
     * @param mail correo a validar
     */
    private fun validMail(mail:String){
        if (!ValidationHelper.validateMail(mail)) {
            ViewHelper.showOneView(errorMailImageView,validMailStatusFrame)
            statusMail= STATUS_MAIL_RED
            showMessage(getString(R.string.view_invalid_form_mail))
            return
        }
        statusMail= STATUS_MAIL_LOADING
        ViewHelper.showOneView(loadServiceMailProgress,validMailStatusFrame)
        RafiServiceWrapper.verifyMail(this, RequestValidateMail(mail),
                { success ->evaluateVerificationServerMail(success)
                },
                {
                    statusMail= STATUS_MAIL_NOT_CALL_SERVICE
                    showMessage(getString(R.string.default_error_server))
                }
        )
    }


    /**
     * Metodo para verificar la respuesta del webService que valida correos
     * @param verification respuesta del webService
     */
    private fun evaluateVerificationServerMail(verification: String) {
        statusMail = if (verification.toUpperCase(Locale.ROOT)=="OK"){
            ViewHelper.showOneView(checkMailImageView,validMailStatusFrame)
            STATUS_MAIL_GREEN
        }else {
            ViewHelper.showOneView(errorMailImageView,validMailStatusFrame)
            showMessage(getString(R.string.view_form_register_step_one_message_mail_in_use))
            STATUS_MAIL_RED
        }
    }

    /**
     * Metodo para evaluar la respuesta del webservice que evalua el DNI
     * @param info respuesta del webservice de validación de DNI
     */
    private fun evaluateDni(info:ResponseValidateDni?) {
        if(info==null|| info.name==null){
            ViewHelper.showOneView(errorNumberDocumentImageView,validNumberDocumentStatusFrame)
            showMessage(getString(R.string.view_form_register_step_two_document_incorrect_Format))
            statusNumberDocument= STATUS_NUMBERDOCUMENT_RED
        }
        if (info!!.name.isNotEmpty()){
            ViewHelper.showOneView(checkNumberDocumentImageView,validNumberDocumentStatusFrame)
            statusNumberDocument= STATUS_NUMBERDOCUMENT_GREEN
            etName.setText(info.name)
            etPaterno.setText("${info.first_name} ${info.last_name} ")
        }
    }

    /**
     * Método para llenar los spinner iniciales y configurar sus eventos
     */
    private fun configureSpinner(){
        spDepartamento.onItemSelectedListener =   getListenerSpinner { position->spinnerItemSelectedDepartamento(position) }
        spProvincia.onItemSelectedListener = getListenerSpinner { position->spinnerItemSelectProvincia(position) }
        spDistrito.onItemSelectedListener = getListenerSpinner { position->spinnerItemSelectDistrito(position) }
        val departamentoAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaDepartamento!!.toMutableList())
        spDepartamento.adapter = departamentoAdapter
        fillListLanguage()
    }


    /**
     * Método que invoca un webservice para obtener los dialectos para el registro
     */
    private fun fillListLanguage(){
        val progress = Util.createProgressDialog(this, "Cargando")
        progress.show()
        RafiServiceWrapper.getLanguage(this, {
            list->
            this.listLanguage= ArrayList(list)
            val langueageAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,list.toMutableList())
            spDialecto.adapter=langueageAdapter
            spDialecto.onItemSelectedListener = getListenerSpinner { position->this.idDialect= listLanguage!![position].language_id}
            progress.dismiss()
        },{error->
            progress.dismiss()
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        })
    }

    /**
     * Método que devuelve un listener génerico para los registros
     * @return Listener Génerico para el registro
     */
    private fun getListenerSpinner(itemSelect: (position: Int) -> Unit): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                itemSelect(position)
            }
        }
    }

    /**
     * Metodo que indica la acción a realizar cuando se selecciona un departamento
     * @param position posicion de la lista del departamento seleccionado
     */
    private fun spinnerItemSelectedDepartamento(position: Int) {
        val ubigeo = spDepartamento.getItemAtPosition(position) as Ubigeo
        departamento = ubigeo.nombre
        var listaProvincia: ArrayList<Ubigeo> = ArrayList()
        wrapperQueryDataBase(SEARCH_PROVINCIA,ubigeo.idDepartamento){ x->  listaProvincia =x!! }
        val provinciaAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaProvincia)
        spProvincia.adapter = provinciaAdapter
    }

    /**
     * Metodo que indica la acción a realizar cuando se selecciona una provincia
     * @param position posicion de la lista de la provincia seleccionada
     */
    private fun spinnerItemSelectProvincia(position: Int) {
        val ubigeo = spProvincia.getItemAtPosition(position) as Ubigeo
        provincia = ubigeo.nombre
        var listaDistrito: ArrayList<Ubigeo> = ArrayList()
        wrapperQueryDataBase(SEARCH_DISTRITO,ubigeo.idDepartamento,ubigeo.idProvincia){ x->  listaDistrito =x!! }
        val distritoAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaDistrito)
        spDistrito.adapter = distritoAdapter
        btnSignup.setOnClickListener { registrarUser() }
    }

    /**
     * Metodo que indica la acción a realizar cuando se selecciona una provincia
     * @param position posicion de la lista de la provincia seleccionada
     */
    private fun spinnerItemSelectDistrito(position: Int) {
        val ubigeo = spProvincia.getItemAtPosition(position) as Ubigeo
        distrito = departamento + "/" + provincia + "/" + ubigeo.nombre
        ubigeoSelected = ubigeo
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Metodo que valida los datos del registro de usuario, si son correctos invoca un metodo para registrar al usuario,
     * caso contrario marca el error en la vista
     */
    private fun registrarUser() {
        if(statusMail!= STATUS_MAIL_GREEN){
            showError(etEmail, getString(R.string.registro_message_error_correo_electronico_invalido))
            return
        }
        if(statusNumberDocument!= STATUS_NUMBERDOCUMENT_GREEN){
            showError(etDni, getString(R.string.registro_message_error_formato_dni))
            return
        }

        if (hasErrorEditTextEmpty(etEmail,R.string.registro_message_error_ingrese_correo_electronico)) return
        if (hasErrorEditTextEmpty(etPassword,R.string.registro_message_error_ingrese_contrasena_usuario)) return
        if (hasErrorEditTextEmpty(etPaterno,R.string.registro_message_error_ingrese_apellido_paterno)) return
        if (hasErrorEditTextEmpty(etName,R.string.registro_message_error_ingrese_nombres)) return
        if (hasErrorEditTextEmpty(etDni,R.string.registro_message_error_formato_dni)) return
        if (hasErrorEditTextEmpty(etTelefono,R.string.registro_message_error_formato_phone)) return
        registrar()
    }

    /**
     * Metodo para registrar el usuario mediante un Webservice
     */
    private fun registrar() {
        val usuario = User()
        usuario.firstName = etName.text.toString()
        usuario.lastName = etPaterno.text.toString()
        usuario.email = etEmail.text.toString()
        usuario.password = etPassword.text.toString()
        usuario.phone = etTelefono.text.toString()
        usuario.dni = etDni.text.toString()
        usuario.codeDepartamento=ubigeoSelected?.idDepartamento
        usuario.codeProvincia=ubigeoSelected?.idProvincia
        usuario.codeDistrito=ubigeoSelected?.idDistrito
        usuario.avance=0
        usuario.idLanguage=idDialect
        registerByServiceWeb(usuario)
    }

    /**
     * Metodo que invoca el webService para registrar el usuario
     * @param user objeto del tipo Usuario con la información del usuario
     */
    private fun registerByServiceWeb(user:User){
        val progress = Util.createProgressDialog(this, "Cargando")
        progress.show()
        RafiServiceWrapper.registerUser(this, GeneralMapper.userToRegisterUserDto(user),{
            progress.dismiss()
            verifyLoginExtern(user)
        },{x->
            progress.dismiss()
            Toast.makeText(baseContext, x, Toast.LENGTH_LONG).show()
        })
    }

    /**
     * Metodo para autenticar un usuario
     * @param user datos del usuario a autentificar
     */
    private fun verifyLoginExtern(user: User) {
        val progress = Util.createProgressDialog(this, "Cargando")
        progress.show()
        RafiServiceWrapper.loginUser(this,
                LoginRequestDto(email = user.email, password = user.password),
                { loginUser -> createSession(GeneralMapper.loginUserDtoDtoToUser(loginUser)) },
                { error ->
                    progress.dismiss()
                    Toast.makeText(baseContext, error, Toast.LENGTH_LONG).show()
                })

    }

    /**
     * Metodo para crear una sesión del usuario y llevarlo a la vista principal
     */
    private fun createSession(user: User){
        SessionManager.getInstance(baseContext).createUserSession(user)
        user.userExternId = 0
        setResult(Activity.RESULT_OK, null)
        finish()
        goToActivity()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    /**
     * Metodo para obtener los ubigeos de la base de datos interna
     * @param typeUbigeo=Tipo de ubigeo
     * @param idDepartamento=Id del departamento a obtener
     * @param idProvincia Id de la provincia
     * @param getUbigeo lambda con donde se coloca la acción a realizar cuando se obtuvo exito en la consulta
     */
    private fun wrapperQueryDataBase(typeUbigeo:Int,idDepartamento:Int=0,idProvincia:Int=0,getUbigeo: (listUbigeo:  ArrayList<Ubigeo>? ) -> Unit){
        val dataBaseService = DataBaseService.getInstance(this)
        try {
            dataBaseService.listDepartamento
            val listUbigeo=when(typeUbigeo){
                SEARCH_DEPARTMENT -> dataBaseService.listDepartamento
                SEARCH_PROVINCIA-> dataBaseService.getListProvincia(idDepartamento)
                SEARCH_DISTRITO->  dataBaseService.getListDistrito(idDepartamento, idProvincia)
                else->throw java.lang.Exception()
            }
            getUbigeo(listUbigeo )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

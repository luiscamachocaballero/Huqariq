package com.itsigned.huqariq.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.itsigned.huqariq.R
import com.itsigned.huqariq.adapter.MyStepperAdapter
import com.itsigned.huqariq.bean.User
import com.itsigned.huqariq.helper.getLoadingProgress
import com.itsigned.huqariq.helper.goToActivity
import com.itsigned.huqariq.mapper.GeneralMapper
import com.itsigned.huqariq.model.FormRegisterStepThreeDto
import com.itsigned.huqariq.model.FormRegisterUserStepOneDto
import com.itsigned.huqariq.model.FormRegisterUserStepTwoDto
import com.itsigned.huqariq.model.LoginRequestDto
import com.itsigned.huqariq.serviceclient.RafiServiceWrapper
import com.itsigned.huqariq.util.Util
import com.itsigned.huqariq.util.session.SessionManager
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.stepstone.stepper.internal.widget.TabsContainer
import com.stepstone.stepper.viewmodel.StepViewModel
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.ArrayList


const val STATUS_NUMBERDOCUMENT_NOT_CALL_SERVICE=0
const val STATUS_NUMBERDOCUMENT_GREEN=1
const val STATUS_NUMBERDOCUMENT_RED=2
const val STATUS_NUMBERDOCUMENT_LOADING=3


class RegisterActivity : AppCompatActivity(),GetFormDataStepperAction, StepperLayout.StepperListener {


    private var statusNumberDocument:Int=0

    lateinit var customProgressDialog: Dialog

    private val mapValues=HashMap<String,String>()
    private var stepPosition=0

    private var formRegisterUserStepOneDto:FormRegisterUserStepOneDto?=null
    private var formRegisterUserStepTwoDto:FormRegisterUserStepTwoDto?=null
    private var formRegisterUserStepThreeDto:FormRegisterStepThreeDto?=null

    /**
     * Metodo para la creación de activitys
     * @param savedInstanceState Bundle con información de la actividad previa
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
//        setupToolbar()

        customProgressDialog=getLoadingProgress()
        if(intent.extras!=null&&intent.extras!!.containsKey("email")){
            mapValues["email"]=intent.getStringExtra("email")
        }
        stepperLayout.adapter= MyStepperAdapter(supportFragmentManager, this,this)
        stepperLayout.findViewById<View>(R.id.ms_stepPrevButton).visibility=View.GONE
        stepperLayout.setListener(this)
       // stepperLayout.bac
        stepperLayout.onBackClicked()


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

    /**
     * Metodo para registrar el usuario mediante un Webservice
     */
    private fun registrar() {
        val usuario = User()
        /*
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

         */

        usuario.firstName = formRegisterUserStepOneDto!!.name
        usuario.lastName = formRegisterUserStepOneDto!!.surname
        usuario.email = formRegisterUserStepOneDto!!.email
        usuario.password = formRegisterUserStepOneDto!!.password
        usuario.phone = ""
        usuario.dni = formRegisterUserStepOneDto!!.dni
        usuario.codeDepartamento=formRegisterUserStepTwoDto!!.regionId.toInt()
        usuario.codeProvincia=formRegisterUserStepTwoDto!!.provinciaId.toInt()
        usuario.codeDistrito=formRegisterUserStepTwoDto!!.distritoId.toInt()
        usuario.avance=0
        usuario.idLanguage=formRegisterUserStepThreeDto!!.idDialect.toInt()
        Log.d("user for register",usuario.toString())
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



    override fun onStepSelected(newStepPosition: Int) {
        this.stepPosition=newStepPosition
        stepperLayout.findViewById<View>(R.id.ms_stepPrevButton).visibility=View.GONE

    }

    override fun onError(verificationError: VerificationError?) {
    }

    override fun onReturn() {
    }

    override fun onCompleted(completeButton: View?) {
        registrar()
    }

    override fun getValues(): HashMap<String, String> {
        return mapValues
    }

    override fun changeQuantityTab(quantity: Int) {
        (stepperLayout.adapter as MyStepperAdapter).changeCount(quantity)
        updateTabContainer(quantity)
    }

    @SuppressLint("RestrictedApi")
    private fun updateTabContainer(quantity: Int){
        val tabContainer=stepperLayout.findViewById(R.id.ms_stepTabsContainer) as TabsContainer
        val list=ArrayList<StepViewModel>()
        for (i in 1..quantity){list.add(StepViewModel.Builder(this).create())}
        tabContainer.setSteps(list)
        tabContainer.performClick()
        stepperLayout.setShowBottomNavigation(true)
        val error= SparseArray<VerificationError>()
        tabContainer.updateSteps(stepperLayout.currentStepPosition,error,false)

    }


    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun setDataFormSteperOne(form: FormRegisterUserStepOneDto) { formRegisterUserStepOneDto=form }

    override fun setDataFormSteperTwo(form: FormRegisterUserStepTwoDto) {formRegisterUserStepTwoDto=form}

    override fun setDataFormSteperThree(form: FormRegisterStepThreeDto) {formRegisterUserStepThreeDto=form}


}

interface GetFormDataStepperAction{
    fun setDataFormSteperOne(form:FormRegisterUserStepOneDto)
    fun setDataFormSteperTwo(form:FormRegisterUserStepTwoDto)
    fun setDataFormSteperThree(form:FormRegisterStepThreeDto)
    fun getValues():HashMap<String,String>
    fun changeQuantityTab(quantity:Int)

}
package com.itsigned.huqariq.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.itsigned.huqariq.R
import com.itsigned.huqariq.activity.*
import com.itsigned.huqariq.helper.ValidationHelper
import com.itsigned.huqariq.helper.ViewHelper
import com.itsigned.huqariq.helper.setErrorEditText
import com.itsigned.huqariq.helper.showMessage
import com.itsigned.huqariq.model.FormRegisterUserStepOneDto
import com.itsigned.huqariq.model.RequestValidateDni
import com.itsigned.huqariq.model.RequestValidateMail
import com.itsigned.huqariq.model.ResponseValidateDni
import com.itsigned.huqariq.serviceclient.RafiServiceWrapper
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.fragment_step_one.*
import java.util.*

const val STATUS_MAIL_NOT_CALL_SERVICE=0
const val STATUS_MAIL_GREEN=1
const val STATUS_MAIL_RED=2
const val STATUS_MAIL_LOADING=3

class StepOneFragment : StepFragment() {


    private var statusMail: Int=0
    var action: GetFormDataStepperAction?=null
    private var statusNumberDocument:Int=0



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_step_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        statusMail=STATUS_MAIL_NOT_CALL_SERVICE
        if (action!!.getValues().containsKey("email")){
            mailEditText.setText(action!!.getValues()["email"])
            mailEditText.isEnabled=false
        }
        mailEditText.onFocusChangeListener= View.OnFocusChangeListener {
            _, hasFocus -> if(!hasFocus)validMail(mailEditText.text.toString())
        }
        statusNumberDocument= STATUS_NUMBERDOCUMENT_NOT_CALL_SERVICE
        etDni.onFocusChangeListener= View.OnFocusChangeListener {
            _, hasFocus -> if(!hasFocus)validateNumberDocumentWithServer(etDni.text.toString())
        }
    }

    private fun validMail(mail:String){

        if (!ValidationHelper.validateMail(mail)) {
            ViewHelper.showOneView(errorMailImageView,validMailStatusFrame)
            statusMail= STATUS_MAIL_RED
            showMessage(getString(R.string.view_invalid_form_mail))
            return
        }
        statusMail= STATUS_MAIL_LOADING
        action!!.enabledNext(false)
        ViewHelper.showOneView(loadServiceMailProgress,validMailStatusFrame)
        RafiServiceWrapper.verifyMail(context!!, RequestValidateMail(mail),
                { success ->
                    evaluateVerificationServerMail(success)
                    updateButtonNextForLoading()

                },
                {

                    statusMail= STATUS_MAIL_NOT_CALL_SERVICE
                    showMessage(getString(R.string.default_error_server))
                }
        )
    }

    private fun validateNumberDocumentWithServer(numberDocument: String) {
        if (!ValidationHelper.validateIdentifyNumber(numberDocument)) {
            ViewHelper.showOneView(errorNumberDocumentImageView,validNumberDocumentStatusFrame)
            statusNumberDocument= STATUS_NUMBERDOCUMENT_RED
            showMessage(getString(R.string.view_form_register_step_two_document_incorrect_Format))
            return
        }
        statusNumberDocument= STATUS_NUMBERDOCUMENT_LOADING
        action!!.enabledNext(false)

        ViewHelper.showOneView(loadServiceNumberDocumentProgress,validNumberDocumentStatusFrame)
        RafiServiceWrapper.validateDni(context!!, RequestValidateDni(etDni.text.toString()),
                { success ->evaluateDni(success)
                    updateButtonNextForLoading()
                },
                {
                    statusNumberDocument= STATUS_NUMBERDOCUMENT_NOT_CALL_SERVICE
                    showMessage(getString(R.string.default_error_server))
                }
        )
    }

    private fun evaluateDni(info: ResponseValidateDni?) {
        if(info==null|| info.name==null){
            ViewHelper.showOneView(errorNumberDocumentImageView,validNumberDocumentStatusFrame)
            showMessage(getString(R.string.view_form_register_step_two_document_incorrect_Format))
            statusNumberDocument= STATUS_NUMBERDOCUMENT_RED
        }
        if (info!!.name.isNotEmpty()){
            ViewHelper.showOneView(checkNumberDocumentImageView,validNumberDocumentStatusFrame)
            statusNumberDocument= STATUS_NUMBERDOCUMENT_GREEN
            nameEditText.setText(info.name)
            surnameEditText.setText("${info.first_name} ${info.last_name} ")
        }
    }



    private fun evaluateVerificationServerMail(verification: String) {
        statusMail = if (verification.toUpperCase(Locale.ROOT)=="OK"){
            updateButtonNextForLoading()
            ViewHelper.showOneView(checkMailImageView,validMailStatusFrame)
            STATUS_MAIL_GREEN
        }else {
            updateButtonNextForLoading()
            ViewHelper.showOneView(errorMailImageView,validMailStatusFrame)
            showMessage(getString(R.string.view_form_register_step_one_message_mail_in_use))
           STATUS_MAIL_RED
        }
    }

    fun updateButtonNextForLoading(){
       if(statusMail!=STATUS_MAIL_LOADING && statusNumberDocument!= STATUS_NUMBERDOCUMENT_LOADING){
           action!!.enabledNext(true)
       }
    }

    private fun showErrorMail() {setErrorEditText(mailEditText,R.string.registro_message_error_ingrese_correo_electronico) }

    private fun showErrorPassword() {setErrorEditText(passEditText,R.string.registro_message_error_ingrese_contrasena_usuario) }

    private fun showErrorRepeatPassword() {setErrorEditText(passRepeatEditText,R.string.registro_message_error_repeat_usuario) }

    private fun showErrorName() {setErrorEditText(nameEditText,R.string.registro_message_error_ingrese_nombres)}

    private fun showErrorSurname() {setErrorEditText(surnameEditText,R.string.registro_message_error_ingrese_apellido_paterno) }

    private fun showMessage(message: String) {if(isVisible) Toast.makeText(context,message, Toast.LENGTH_LONG).show()}


    override fun verifyStep() {
        val form=getForm()
        action!!.setDataFormSteperOne(form)
        val validForm=validateStepOneRegister(form,statusMail)
        if(validForm)return action!!.goNextStep()
    }



    private fun getForm(): FormRegisterUserStepOneDto {
        return FormRegisterUserStepOneDto(mailEditText.text.toString(),
                passEditText.text.toString(),etDni.text.toString(),
                nameEditText.text.toString(),surnameEditText.text.toString())
    }

    fun validateStepOneRegister(formRegisterStepOne: FormRegisterUserStepOneDto,statusMail:Int):Boolean {


        when{
            statusMail==STATUS_MAIL_LOADING->showMessage(context!!.getString(R.string.registro_message_error_correo_electronico_invalido))
            statusMail== STATUS_MAIL_RED->showMessage(context!!.getString(R.string.registro_message_error_correo_electronico_invalido))
            !ValidationHelper.validateMail(formRegisterStepOne.email)->showErrorMail()
            !ValidationHelper.validatePassword(formRegisterStepOne.password)->showErrorPassword()
            formRegisterStepOne.password!=passRepeatEditText.text.toString()->showErrorRepeatPassword()
            !ValidationHelper.validateStringEmpty(formRegisterStepOne.name)->showErrorName()
            !ValidationHelper.validateStringEmpty(formRegisterStepOne.surname)->showErrorSurname()
            else->return true
        }
        return false
    }

}
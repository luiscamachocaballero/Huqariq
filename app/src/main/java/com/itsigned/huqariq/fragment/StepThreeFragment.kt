package com.itsigned.huqariq.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.itsigned.huqariq.R
import com.itsigned.huqariq.activity.GetFormDataStepperAction
import com.itsigned.huqariq.dialog.PlayAudioDialog
import com.itsigned.huqariq.helper.PermissionHelper
import com.itsigned.huqariq.helper.REQUEST_PERMISION_PLAY_AUDIO
import com.itsigned.huqariq.model.FormRegisterStepThreeDto
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.fragment_step_three.*


class StepThreeFragment : Fragment() , Step {

    var action: GetFormDataStepperAction?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_step_three, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listOptionQuestion = arrayListOf(
                arrayOf("willka","alchhi","irqi","haway","warma"),
                arrayOf("urku","ñawi","mat’i","maki","uma"),
                arrayOf("yaku","t’uru","kachi","yawar","mitu"),
                arrayOf("qiwa","sacha","qhipa","paqu","q’achu"),
                arrayOf("qhilli","qanra","pichay","anka","hawa")

        )
        val listRadioGroup= arrayListOf(radioGroupQuestionOne, radioGroupQuestionTwo,
        radioGroupQuestionThree,radioGroupQuestionFour,radioGroupQuestionFive)

        val listButton= arrayListOf(questionOneButton, questionTwoButton,
                questionThreeButton,questionFourButton,questionFiveButton)


        for (i in 0 until listOptionQuestion.size){
            fillRadioGroup(listRadioGroup[i],listOptionQuestion[i])
            listButton[i].setOnClickListener {
                if(PermissionHelper.playAudioPermission(context!!,this)) openDialog(i)
            }
        }


    }

    private fun fillRadioGroup(radioGroup:RadioGroup,option:Array<String>){
        for (question in option) {
            val radioButton = RadioButton(context!!)
            radioButton.text = question
            radioGroup.addView(radioButton)
        }
    }

    fun openDialog(idRaw:Int){
        val dialog=PlayAudioDialog(idRaw)
        dialog.show(activity!!.supportFragmentManager,"")
    }

    override fun onSelected() {}

    override fun verifyStep(): VerificationError? {
        val validForm=validateStepTwoRegister()
        if(validForm)return null
        val form=getForm()
        action!!.setDataFormSteperThree(form)
        return VerificationError("")
    }

    override fun onError(error: VerificationError) {}


    private fun getForm(): FormRegisterStepThreeDto {
        return FormRegisterStepThreeDto()
    }

    fun validateStepTwoRegister():Boolean {


        when{

            else->return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode== REQUEST_PERMISION_PLAY_AUDIO){

            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context,R.string.message_need_permission, Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,R.string.message_acept_permission, Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
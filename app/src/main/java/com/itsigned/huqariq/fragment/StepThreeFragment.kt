package com.itsigned.huqariq.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
    var quantitySuccessChanka=0
    var quantitySuccessQullawA=0
    var quantitySuccessQullawB=0
    var idDialec=-1

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
        var index=0
        for (question in option) {
            val radioButton = RadioButton(context!!)
            radioButton.text = question
            radioButton.id=index
            index += 1
            radioGroup.addView(radioButton)
        }
        radioGroup.check(0)
    }

    fun openDialog(index:Int){
        val listRaw= arrayListOf(R.raw.questionone,R.raw.questiontwo,R.raw.questionthree,R.raw.questionfour,R.raw.questionfive)
        val message="Presione el boton Play para escuchar la pregunta ${index + 1}"
        val dialog=PlayAudioDialog(listRaw[index],message)
        dialog.show(activity!!.supportFragmentManager,"")
    }

    override fun onSelected() {}

    override fun verifyStep(): VerificationError? {
        val validForm=validateStepTwoRegister()
        val form=getForm()
        action!!.setDataFormSteperThree(form)
        if(validForm)return null
        Toast.makeText(context!!,R.string.message_error_question,Toast.LENGTH_LONG).show()
        return VerificationError("")
    }

    override fun onError(error: VerificationError) {}


    private fun getForm(): FormRegisterStepThreeDto {
        return FormRegisterStepThreeDto(idDialec.toString())
    }

    fun validateStepTwoRegister():Boolean {

        val listResponse=arrayOf(
                radioGroupQuestionOne.checkedRadioButtonId,
                radioGroupQuestionTwo.checkedRadioButtonId,
                radioGroupQuestionThree.checkedRadioButtonId,
                radioGroupQuestionFour.checkedRadioButtonId,
                radioGroupQuestionFive.checkedRadioButtonId
        )
        val listResponseChanca= arrayOf(0,0,4,0,1)
        val listResponseQullaA= arrayOf(1,2,1,4,0)
        val listResponseQullaB= arrayOf(3,2,1,4,0)

        quantitySuccessChanka=0
        quantitySuccessQullawA=0
        quantitySuccessQullawB=0
        for (i in 0..listResponse.size-1){
            if(listResponseChanca[i]==listResponse[i])quantitySuccessChanka++
            if(listResponseQullaA[i]==listResponse[i])quantitySuccessQullawA++
            if(listResponseQullaB[i]==listResponse[i])quantitySuccessQullawB++

        }

        Log.d("success chancha","success chancha ${quantitySuccessChanka}")
        Log.d("success Qullaw A","success chancha ${quantitySuccessQullawA}")
        Log.d("success Qullaw B","success chancha ${quantitySuccessQullawB}")

        if(quantitySuccessChanka<4 && quantitySuccessQullawA<4 && quantitySuccessQullawB<4)return false
        idDialec= if(quantitySuccessQullawA>2 || quantitySuccessQullawB>2) 2 else 1
        return true
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
package com.itsigned.huqariq.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.itsigned.huqariq.R
import com.itsigned.huqariq.activity.GetFormDataStepperAction
import com.itsigned.huqariq.dialog.PlayAudioDialog
import com.itsigned.huqariq.helper.PermissionHelper
import com.itsigned.huqariq.helper.REQUEST_PERMISION_PLAY_AUDIO
import com.itsigned.huqariq.helper.getLoadingProgress
import com.itsigned.huqariq.model.FormDialectAnswer
import com.itsigned.huqariq.model.FormRegisterStepThreeDto
import com.itsigned.huqariq.serviceclient.RafiServiceWrapper
import kotlinx.android.synthetic.main.fragment_step_three.*
import java.util.*


class StepThreeFragment : StepFragment()  {

    var action: GetFormDataStepperAction?=null
    lateinit var customProgressDialog: Dialog

    var idDialec=-1


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







    fun convertCodeAnswerToString(code:Int):String{
        return when(code){
            0->"a"
            1->"b"
            2->"c"
            3->"d"
            4->"e"
            else->""
        }
    }

    override fun verifyStep() {
        Log.d("StepThree","on next")
        var listAnswer =arrayOf(
                convertCodeAnswerToString(radioGroupQuestionOne.checkedRadioButtonId),
                convertCodeAnswerToString(radioGroupQuestionTwo.checkedRadioButtonId),
                convertCodeAnswerToString(radioGroupQuestionThree.checkedRadioButtonId),
                convertCodeAnswerToString(radioGroupQuestionFour.checkedRadioButtonId),
                convertCodeAnswerToString(radioGroupQuestionFive.checkedRadioButtonId)
        )
        customProgressDialog=getLoadingProgress()
        customProgressDialog.show()
        RafiServiceWrapper.validateDialectAnswer(context!!, FormDialectAnswer(listAnswer),
                {x->
                    idDialec = if (x.dialecto.toUpperCase(Locale.ROOT)=="CHANCA") 1
                    else ( if(x.dialecto.toUpperCase(Locale.ROOT) == "COLLAO") 2 else -1)
                    customProgressDialog.dismiss()
                    val form=getForm()
                    action!!.setDataFormSteperThree(form)
                    if(idDialec==-1){
                       // val a=Toast.makeText(context!!,R.string.message_error_question,Toast.LENGTH_LONG)

                        val builder = AlertDialog.Builder(activity!!)
                        builder.setMessage(R.string.message_error_question)
                                .setPositiveButton(R.string.dialog_acept,
                                        DialogInterface.OnClickListener { dialog, id ->
                                            dialog.dismiss()
                                            activity!!.finish()
                                        })

                        // Create the AlertDialog object and return it
                       val d= builder.create()
                        d.setCancelable(false)
                        d.show()

                    }else{
                        this.action!!.goNextStep();
                    }
                },{
            Toast.makeText(context!!,R.string.default_error_server,Toast.LENGTH_LONG).show()
            customProgressDialog.dismiss()

        })

    }




    private fun getForm(): FormRegisterStepThreeDto {
        return FormRegisterStepThreeDto(idDialec.toString())
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
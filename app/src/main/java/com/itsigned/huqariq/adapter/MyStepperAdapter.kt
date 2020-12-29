package com.itsigned.huqariq.adapter

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.itsigned.huqariq.activity.GetFormDataStepperAction
import com.itsigned.huqariq.fragment.*
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel

class MyStepperAdapter(fm: FragmentManager, context: Context,
                       private val action: GetFormDataStepperAction) : AbstractFragmentStepAdapter(fm, context) {

    private var countTabs=3;


    override fun getCount(): Int {
        return countTabs
    }

    override fun createStep(position: Int): Step {



        when(position){

            0->{val step=StepOneFragment()
                step.action=action
                return step
            }
            1->{val step= StepTwoFragment()
                step.action=action
                return step
            }
            2->{val step=StepThreeFragment()
                step.action=action
                return step
            }
            3->{val step=StepFourFragment()
                step.action=action
                return step
            }
            else->{throw Exception("out limit step")}
        }
    }

    fun changeCount(quantityCount:Int){
        createStep(0)
        this.countTabs=quantityCount
        pagerAdapter.notifyDataSetChanged()
       // pagerAdapter.
    }




}
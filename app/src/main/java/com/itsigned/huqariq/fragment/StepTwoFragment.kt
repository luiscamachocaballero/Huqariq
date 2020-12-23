package com.itsigned.huqariq.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.itsigned.huqariq.R
import com.itsigned.huqariq.activity.GetFormDataStepperAction
import com.itsigned.huqariq.bean.Ubigeo
import com.itsigned.huqariq.database.DataBaseService
import com.itsigned.huqariq.helper.showMessage
import com.itsigned.huqariq.model.FormRegisterUserStepTwoDto
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.fragment_step_two.*
import java.util.*
import kotlin.collections.ArrayList

private const val SEARCH_DEPARTMENT=1
private const val SEARCH_PROVINCIA=2
private const val SEARCH_DISTRITO=3

private var listaDepartamento: ArrayList<Ubigeo>? = null
private var departamento: String? = null
private var provincia: String? = null
private var distrito: String? = null
private var ubigeoSelected:Ubigeo?=null

class StepTwoFragment : Fragment() , Step {
    var action: GetFormDataStepperAction?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_step_two, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.wrapperQueryDataBase(SEARCH_DEPARTMENT) { x->listaDepartamento=x }
        configureSpinner()
    }

    /**
     * Metodo para obtener los ubigeos de la base de datos interna
     * @param typeUbigeo=Tipo de ubigeo
     * @param idDepartamento=Id del departamento a obtener
     * @param idProvincia Id de la provincia
     * @param getUbigeo lambda con donde se coloca la acción a realizar cuando se obtuvo exito en la consulta
     */
    private fun wrapperQueryDataBase(typeUbigeo:Int,idDepartamento:Int=0,idProvincia:Int=0,getUbigeo: (listUbigeo:  ArrayList<Ubigeo>? ) -> Unit){
        val dataBaseService = DataBaseService.getInstance(context!!)
        try {
            dataBaseService.listDepartamento
            val listUbigeo=when(typeUbigeo){
                SEARCH_DEPARTMENT -> dataBaseService.listDepartamento
                SEARCH_PROVINCIA -> dataBaseService.getListProvincia(idDepartamento)
                SEARCH_DISTRITO ->  dataBaseService.getListDistrito(idDepartamento, idProvincia)
                else->throw java.lang.Exception()
            }
            getUbigeo(listUbigeo )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Método para llenar los spinner iniciales y configurar sus eventos
     */
    private fun configureSpinner(){
        spDepartamento.onItemSelectedListener =   getListenerSpinner { position->spinnerItemSelectedDepartamento(position) }
        spProvincia.onItemSelectedListener = getListenerSpinner { position->spinnerItemSelectProvincia(position) }
        spDistrito.onItemSelectedListener = getListenerSpinner { position->spinnerItemSelectDistrito(position) }
        val departamentoAdapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, listaDepartamento!!.toMutableList())
        spDepartamento.adapter = departamentoAdapter
    }

    private fun getListenerSpinner(itemSelect: (position: Int) -> Unit): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                itemSelect(position)
            }
        }
    }

    private fun spinnerItemSelectedDepartamento(position: Int) {
        val ubigeo = spDepartamento.getItemAtPosition(position) as Ubigeo
        departamento = ubigeo.nombre
        var listaProvincia: ArrayList<Ubigeo> = ArrayList()
        wrapperQueryDataBase(SEARCH_PROVINCIA,ubigeo.idDepartamento){ x->  listaProvincia =x!! }
        val provinciaAdapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, listaProvincia)
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
        val distritoAdapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, listaDistrito)
        spDistrito.adapter = distritoAdapter

    }

    /**
     * Metodo que indica la acción a realizar cuando se selecciona una provincia
     * @param position posicion de la lista de la provincia seleccionada
     */
    private fun spinnerItemSelectDistrito(position: Int) {
        val ubigeo = spDistrito.getItemAtPosition(position) as Ubigeo
        distrito = departamento + "/" + provincia + "/" + ubigeo.nombre
        ubigeoSelected = ubigeo
        val quantity=if(isSouth())  3 else 4
        action!!.changeQuantityTab(quantity)
    }

    private fun isSouth():Boolean{
        val listRegionSouth= arrayListOf<String>("Apurimac","Ayacucho","Arequipa","Cusco","Puno",
                "Tacna","Moquegua", "Huancavelica")
        val regionFind:String?=listRegionSouth.find { x->x.toUpperCase(Locale.ROOT).equals(departamento!!.toUpperCase(Locale.ROOT)) }
        if(regionFind!=null)return true
        return false
    }

    override fun onSelected() {}

    override fun verifyStep(): VerificationError? {
        val validForm=validateStepTwoRegister()
        if(validForm)return null
        val form=getForm()
        action!!.setDataFormSteperTwo(form)
        return VerificationError("")
    }

    override fun onError(error: VerificationError) {}


    private fun getForm(): FormRegisterUserStepTwoDto {

        return FormRegisterUserStepTwoDto(departamento!!, provincia!!, distrito!!,"",1)
    }

    fun validateStepTwoRegister():Boolean {


        when{
            departamento==null->showMessage(context!!.getString(R.string.register_select_region))
            provincia== null->showMessage(context!!.getString(R.string.register_select_province))
            distrito==null->showMessage(context!!.getString(R.string.register_select_district))
            else->return true
        }
        return false
    }
}





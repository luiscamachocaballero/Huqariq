package com.itsigned.huqariq.activity

import android.os.Bundle
import com.itsigned.huqariq.R
import com.itsigned.huqariq.fragment.RecordAudioFragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.itsigned.huqariq.fragment.ConfigFragment
import com.itsigned.huqariq.util.session.SessionManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var idCurrentFragment: Int = -1

    /**
     * Metodo para la creación de activitys
     * @param savedInstanceState Bundle con información de la actividad previa
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        defaultFragment()
        var fragment: Fragment?
        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            fragment = when (item.itemId) {
                idCurrentFragment -> null
                R.id.record_option -> {
                    item.isChecked = true
                    idCurrentFragment = R.id.record_option
                    supportFragmentManager.findFragmentByTag("record")
                    RecordAudioFragment()

                }

                R.id.config_option -> {
                    idCurrentFragment = R.id.config_option
                    ConfigFragment()
                }

                R.id.close_option->{
                    showDialogCloseSession()
                    null
                }
                else -> null
            }
            fragment.toString()

            transitionFragment(fragment)
        }

    }


    /**
     * Metodo para setear el fragmento por defecto
     */
    private fun defaultFragment() {
        val fragmentToView = RecordAudioFragment()
        transitionFragment(fragmentToView)
        bottomNavigation.selectedItemId = 0
    }

    /**
     * Metodo para transicionar el fragmento dado
     * @param fragment el fragmento a transicionar
     * @return un boleano indicando el exito de la transacción
     */
    private fun transitionFragment(fragment: Fragment?): Boolean {
        if (fragment == null) return false
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentTransition, fragment, "Fragment")
        transaction.addToBackStack(fragment.tag)
        transaction.commit()
        return true
    }

    /**
     * Método que muestra un dialogo para cerrar la sesión
     */
    private fun showDialogCloseSession(){

        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("¿Desea cerrar la sesión?")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
                "Si"
        ) { dialog, id ->
            finish()
            SessionManager.getInstance(this).logoutApp()
            dialog.cancel() }

        builder1.setNegativeButton(
                "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()
    }




}




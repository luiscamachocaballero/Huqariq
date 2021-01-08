package com.itsigned.huqariq.serviceclient

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.itsigned.huqariq.R
import com.itsigned.huqariq.helper.SystemFileHelper
import com.itsigned.huqariq.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.lang.Exception

const val TAG = "RafiServiceWrapper"
const val CONTENT_TYPE_JSON="application/json"

/**
 * Clase con metodos para invocar webService
 */
class RafiServiceWrapper {

    companion object{

        /**
         * Metodo para invocar al servicio de logueo de usuario
         * @param context contexto de la aplicacion
         * @param body cuerpo de la peticion, contiene el usuario y password para el logueo
         * @param onSuccess metodo para invocar si la peticion al servicio es exitosa
         * @param onError metodo para invocar si la peticion es erronea
         */
        fun loginUser(context: Context, body: LoginRequestDto, onSuccess: (success: LoginUserDto) -> Unit, onError: (error: String) -> Unit) {
            Log.d(TAG,"execute service loginUser whith")
            Log.d(TAG,body.toString())
            val apiService = RafiService.create()
            apiService.loginApp(CONTENT_TYPE_JSON,body)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { result -> onSuccess(result) },
                            { error ->
                                Log.d(TAG, "error loginUser")
                                error.printStackTrace()

                                if ((error as HttpException).code() == 400) {
                                    onError(context.getString(R.string.error_login))
                                } else {
                                    onError(context.getString(R.string.generic_error))
                                }


                            }

                    )
        }


        /**
         * Metodo para obtener los dialectos del servicio web
         * @param context contexto de la aplicacion
         * @param onSuccess metodo para invocar si la peticion al servicio es exitosa
         * @param onError metodo para invocar si la peticion es erronea
         */
        fun getLanguage(context: Context, onSuccess: (success: List<Language>) -> Unit, onError: (error: String) -> Unit) {
            val apiService = RafiService.create()
            Log.d(TAG,"execute service getLanguage")
            apiService.getLanguage(CONTENT_TYPE_JSON)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { result -> onSuccess(result.language) },
                            { error ->
                                Log.d(TAG,"error language")
                                error.printStackTrace()
                                onError(context.getString(R.string.generic_error)) }
                    )
        }

        /**
         * Metodo para invocar al servicio de registro de usuario
         * @param context contexto de la aplicacion
         * @param body cuerpo de la peticion, contiene los datos de registro
         * @param onSuccess metodo para invocar si la peticion al servicio es exitosa
         * @param onError metodo para invocar si la peticion es erronea
         */
        fun registerUser(context: Context, body: RegisterUserDto, onSuccess: (success: RegisterUserDto) -> Unit, onError: (error: String) -> Unit) {
            val apiService = RafiService.create()
            Log.d(TAG,"execute service register whith")
            Log.d(TAG,body.toString())
            apiService.register(CONTENT_TYPE_JSON,body)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { result -> onSuccess(result) },
                            { error ->
                                Log.d(TAG,"error register user")
                                error.printStackTrace()
                                onError(context.getString(R.string.error_register)) }
                    )
        }

        /**
         * Metodo para invocar al servicio de validación de DNI
         * @param context contexto de la aplicacion
         * @param body cuerpo de la peticion, contiene los datos de DNI
         * @param onSuccess metodo para invocar si la peticion al servicio es exitosa
         * @param onError metodo para invocar si la peticion es erronea
         */
        fun validateDni(context: Context, body: RequestValidateDni, onSuccess: (success: ResponseValidateDni) -> Unit, onError: (error: String) -> Unit) {
            val apiService = RafiService.create()
            Log.d(TAG,"execute service register whith")
            Log.d(TAG,body.toString())
            apiService.validateDni(body)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { result -> onSuccess(Gson().fromJson(result.string()
                                    .replace("\\","")
                                    .replace("\"{","{")
                                    .replace("}\"","}"),ResponseValidateDni::class.java)) },
                            { error ->
                                Log.d(TAG,"error register user")
                                error.printStackTrace()
                                onError(context.getString(R.string.generic_error)) }
                    )
        }

        fun validateDialectByRegion(context: Context, body: FormDialectRegion, onSuccess: (success: ResponseDialectRegion) -> Unit, onError: (error: String) -> Unit) {
            val apiService = RafiService.create()
            Log.d(TAG,"execute service validateDialectByRegion whith")
            Log.d(TAG,body.toString())
            apiService.validateDialectByRegion(body)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { result -> onSuccess(result) },
                            { error ->
                                Log.d(TAG,"error validate register")
                                error.printStackTrace()
                                onError(context.getString(R.string.generic_error)) }
                    )
        }

        fun validateDialectAnswer(context: Context, body: FormDialectAnswer, onSuccess: (success: ResponseDialectAnswer) -> Unit, onError: (error: String) -> Unit) {
            val apiService = RafiService.create()
            Log.d(TAG,"execute service validateDialectAnswer whith")
            Log.d(TAG,body.toString())
            apiService.validateAnswerDialecto(body)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { result -> onSuccess(result) },
                            { error ->
                                Log.d(TAG,"error validate validateDialectAnswer")
                                error.printStackTrace()
                                onError(context.getString(R.string.generic_error)) }
                    )
        }

        /**
         * Metodo para invocar al servicio de validación de correo
         * @param context contexto de la aplicacion
         * @param body cuerpo de la peticion, contiene los datos de correo
         * @param onSuccess metodo para invocar si la peticion al servicio es exitosa
         * @param onError metodo para invocar si la peticion es erronea
         */
        fun verifyMail(context: Context, body: RequestValidateMail, onSuccess: (success: String) -> Unit, onError: (error: String) -> Unit) {
            val apiService = RafiService.create()
            Log.d(TAG,"execute service verifyMail whith")
            Log.d(TAG,body.toString())
            apiService.verifyMail(body)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { result -> onSuccess(result.string()) },
                            { error ->
                                Log.d(TAG,"error register user")
                                error.printStackTrace()
                                onError(context.getString(R.string.generic_error)) }
                    )
        }

        /**
         * Metodo para invocar al servicio de subir un arhivo de audio
         * @param fileName nombre del archivo de audio
         * @param requestBody cuerpo de la peticion, contiene los datos del archivo de audio
         * @param context contexto de la aplicación
         * @param onSuccess metodo para invocar si la peticion al servicio es exitosa
         * @param onError metodo para invocar si la peticion es erronea
         */
    fun uploadAudio(fileName:String, requestBody: RequestBody, context: Context,
                    onSuccess: () -> Unit, onError: (error: String?) -> Unit) {
        Log.d(TAG,"execute service uploadAudio whith name"+fileName)
        Log.d(TAG,requestBody.toString())
        val part = MultipartBody.Part.createFormData("files", fileName, requestBody)
        val apiService = RafiService.create()
        apiService.uploadAudio(part)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({  onSuccess()
                }, { error ->
                    Log.d(TAG,"error upload Audio")
                    error.printStackTrace()
                    onError(context.getString(R.string.generic_error))
                }
                )
    }

        /**
         * Metodo para invocar al servicio de descargar un audio de muestra
         * @param context contexto de la aplicación
         * @param url url del audio a descargar
         * @param nameDirectory nombre del directorio donde se guardará el archivo descargado
         * @param nameFile nombre con el cual se guardará el archivo descargado
         * @param onSuccess metodo para invocar si la peticion al servicio es exitosa
         * @param onError metodo para invocar si la peticion es erronea
         */
    fun downloadFile(context: Context, url: String, nameDirectory: String, nameFile: String,
                     onSuccess: () -> Unit, onError: (error: String?) -> Unit) {
        Log.d(TAG, "execute service download  whith url $url")
        val apiService = RafiService.createForFile()
        apiService.downloadAudio("", url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { result ->
                            SystemFileHelper.writeResponseBodyToDisk(context, result, nameDirectory, nameFile)
                            onSuccess()
                        },
                        { error ->
                            error.printStackTrace()
                            onError(context.getString(R.string.generic_error))
                        })

    }
    }
}

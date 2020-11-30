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

const val TAG = "RafiServiceWrapper"
const val CONTENT_TYPE_JSON="application/json"
class RafiServiceWrapper {

    companion object{
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
                                Log.d(TAG,"error loginUser")
                                error.printStackTrace()
                                onError(context.getString(R.string.generic_error)) }
                    )
        }

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
                                onError(context.getString(R.string.generic_error)) }
                    )
        }

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

    fun uploadAudio(fileName:String, requestBody: RequestBody, context: Context,
                    onSuccess: () -> Unit, onError: (error: String?) -> Unit) {
        Log.d(TAG,"execute service uploadAudio whith")
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

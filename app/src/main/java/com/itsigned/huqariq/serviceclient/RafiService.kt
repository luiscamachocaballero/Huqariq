package com.itsigned.huqariq.serviceclient

import android.util.Log
import com.itsigned.huqariq.model.*
import com.itsigned.huqariq.util.HttpRequestConstants.Companion.URL_BASE
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

const val TIME_OUT_MINUTE=3

/**
 * clase con la interface del Retrofit
 */
interface RafiService {

    @POST("/account_app")
    fun register(@Header("Content-Type")  contentType:String, @Body body: RegisterUserDto): Observable<RegisterUserDto>

    @POST("/login_app")
    fun loginApp(@Header("Content-Type")  contentType:String, @Body body: LoginRequestDto): Observable<LoginUserDto>

    @Multipart
    @POST("/upload_app")
    fun uploadAudio(@Part file: MultipartBody.Part): Observable<Any>

    @POST("/lang")
    fun getLanguage(@Header("Content-Type")  contentType:String): Observable<ResponseLangDto>

    @GET("/prompts_audios/{user_id}")
    fun downloadAudio(
            @Header("Cookie") session: String,
            @Path(value = "user_id")  userId:String
    ): Observable<ResponseBody>

    @POST("/vemail")
    fun verifyMail(@Body body: RequestValidateMail): Observable<ResponseBody>

    @POST("/dni")
    fun validateDni(@Body body: RequestValidateDni): Observable<ResponseBody>


    @POST("/dialecto_region")
    fun validateDialectByRegion(@Body body: FormDialectRegion): Observable<ResponseDialectRegion>

    @POST("/dialecto")
    fun validateAnswerDialecto(@Body body: FormDialectAnswer): Observable<ResponseDialectAnswer>


    companion object Factory {
        fun create(): RafiService {
            Log.d("URL BASE", URL_BASE)
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(URL_BASE)
                    .build()
            return retrofit.create(RafiService::class.java)
        }

        fun createForFile(): RafiService {
            val client = OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT_MINUTE.toLong(), TimeUnit.MINUTES).
                    writeTimeout(TIME_OUT_MINUTE.toLong(), TimeUnit.MINUTES)
                    .readTimeout(TIME_OUT_MINUTE.toLong(), TimeUnit.MINUTES).build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(URL_BASE)
                    .client(client)
                    .build()
            return retrofit.create(RafiService::class.java)
        }

    }
}
package com.cloudin.monsterchicken.basedata.source.remote

import android.os.Build
import com.cloudin.monsterchicken.basedata.CommonResult
import com.cloudin.monsterchicken.basedata.TLSSocketFactory
import com.cloudin.monsterchicken.utils.NativeUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit


interface CloudInCommonApi {

    @POST
    @Headers("Content-Type: application/json; charset=urf-8")
    suspend fun commonPost(
        @Url urlPath: String?,
        @Body inputBody: Map<String, String>?,
        @Header("Authorization") authHeader: String?
    ): Response<CommonResult>

    @PUT
    @Headers("Content-Type: application/json; charset=urf-8")
    suspend fun commonPut(
        @Url urlPath: String?,
        @Body inputBody: Map<String, String>?,
        @Header("Authorization") authHeader: String?
    ): Response<CommonResult>

    @GET
    suspend fun commonGet(
        @Url urlPath: String?,
        @Query("input") inputBody: String,
        @Header("Authorization") authHeader: String?
    ): Response<CommonResult>

    @OPTIONS
    suspend fun commonOptions(
        @Url urlPath: String?,
        @Query("input") inputBody: String,
        @Header("Authorization") authHeader: String?
    ): Response<CommonResult>

    @DELETE
    suspend fun commonDelete(
        @Url urlPath: String?,
        @Header("Authorization") authHeader: String?
    ): Response<CommonResult>

    @Multipart
    @POST
    suspend fun uploadImage(
        @Url urlPath: String,
        @Header("Authorization") authHeader: String?,
        @Part image: MultipartBody.Part
    ): Response<CommonResult>


    companion object {
        operator fun invoke(): CloudInCommonApi {
            return Retrofit.Builder()
                .client(getClient()!!)
                .addConverterFactory(GsonConverterFactory.create(getGson()!!))
                .baseUrl(NativeUtils.getAppDomainURL()!!)
                .build()
                .create(CloudInCommonApi::class.java)
        }

        private fun getGson(): Gson? {
            return GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation().create()
        }

        private fun getClient(): OkHttpClient? {
            var okHttpClient: OkHttpClient? = null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                try {
                    okHttpClient = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(40, TimeUnit.SECONDS)// old id 20
                        .writeTimeout(40, TimeUnit.SECONDS) //old is 10
                        .build()
                } catch (e: KeyStoreException) {
                    e.printStackTrace()
                } catch (e: KeyManagementException) {
                    e.printStackTrace()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                } catch (e: CertificateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                var tlsTocketFactory: TLSSocketFactory? = null
                try {
                    tlsTocketFactory = TLSSocketFactory()
                    okHttpClient = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(40, TimeUnit.SECONDS) // old is 20
                        .writeTimeout(40, TimeUnit.SECONDS) // old is 10
                        .sslSocketFactory(tlsTocketFactory, tlsTocketFactory.trustManager!!)
                        .build()
                } catch (e: KeyStoreException) {
                    e.printStackTrace()
                } catch (e: KeyManagementException) {
                    e.printStackTrace()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                } catch (e: CertificateException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            return okHttpClient
        }


        val gsonConverter: GsonConverterFactory
            get() {
                var mGsonConverter: GsonConverterFactory? = null
                if (mGsonConverter == null) {
                    mGsonConverter = GsonConverterFactory
                        .create(
                            GsonBuilder()
                                .setLenient()
                                .disableHtmlEscaping()
                                .create()
                        )
                }
                return mGsonConverter!!
            }
    }

}
package com.cloudin.monsterchicken.basedata.source.remote

import com.cloudin.monsterchicken.baseApiCalls.LoginResponse
import com.cloudin.monsterchicken.basedata.CommonResult
import com.cloudin.monsterchicken.basedata.Result
import com.cloudin.monsterchicken.utils.CloudInCrypto
import com.cloudin.monsterchicken.utils.NativeUtils
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeoutException

abstract class CloudInApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                Result.Success(response.code(), response.body()!!)
            } else {
                val responseCode: Int = response.code()
                val errorMessage = arrayListOf<String>()
                if (response.code() == 400 || response.code() == 422) {
                    val responseString =
                        Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            CommonResult::class.java
                        )
                    val decryptResult = CloudInCrypto.aesDecrypt(responseString.result)
                    val resultResponse =
                        NativeUtils.convertStringToPojo(
                            decryptResult,
                            LoginResponse::class.java
                        )
                    errorMessage.addAll(resultResponse.message!!)
                } else {
                    errorMessage.add("")
                }

                Result.Error(
                    responseCode,
                    responseCode.toString(),
                    errorMessage
                )
            }
        } catch (ex: CancellationException) {
            Result.Error(100, "Api Exception ", arrayListOf("Timeout"))
        } catch (ex: TimeoutException) {
            Result.Error(100, "Api Exception ", arrayListOf("Timeout"))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(0, "Api Exception ", arrayListOf(e.message!!))
        }
    }
}

class ApiException(message: String) : IOException(message)
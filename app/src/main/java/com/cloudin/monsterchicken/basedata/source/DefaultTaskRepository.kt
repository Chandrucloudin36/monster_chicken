package com.cloudin.monsterchicken.basedata.source

import android.content.Context
import com.cloudin.monsterchicken.basedata.CommonResult
import com.cloudin.monsterchicken.basedata.Result
import com.cloudin.monsterchicken.basedata.source.remote.CloudInApiRequest
import com.cloudin.monsterchicken.basedata.source.remote.CloudInCommonApi
import com.cloudin.monsterchicken.utils.APP_NAME
import com.cloudin.monsterchicken.utils.APP_VERSION
import com.cloudin.monsterchicken.utils.CloudInCrypto
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.DeviceUtils
import com.cloudin.monsterchicken.utils.INPUT_NAME
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.NativeUtils.toMap
import com.cloudin.monsterchicken.utils.USER_AUTH_TOKEN
import com.cloudin.monsterchicken.utils.USER_ID
import com.cloudin.monsterchicken.utils.USER_LAT
import com.cloudin.monsterchicken.utils.USER_LONG
import com.cloudin.monsterchicken.utils.USER_UNIQUE_TOKEN
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DefaultTaskRepository(
    private val apiCall: CloudInCommonApi
) : CloudInApiRequest(), TaskRepository {

    private fun addCommonDetail(jsonObject: JSONObject, context: Context) {
        val calendar: Calendar? = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        try {
            jsonObject.put("platform", "android")
            jsonObject.put("requestFrom", "mobile")
            jsonObject.put("app_version_code", APP_VERSION)
            jsonObject.put("app_version", APP_NAME.toDouble())
            jsonObject.put("GradleVersion", APP_NAME.toDouble())
            jsonObject.put("deviceId", DeviceUtils.getAndroidID(context))
            jsonObject.put("BatteryStatus", DeviceUtils.getBatteryStatus(context))
            jsonObject.put("Language", Locale.getDefault().language)
            jsonObject.put("Country", Locale.getDefault().country)
            jsonObject.put("DeviceOffset", (DeviceUtils.getDeviceOffset() / 1000).toString())
            jsonObject.put("DeviceDateTime", sdf.format(calendar!!.time))
            jsonObject.put("AccessToken", CloudInPreferenceManager.getString(USER_AUTH_TOKEN, ""))
            jsonObject.put("NetworkType", DeviceUtils.getNetworkType(context))
            jsonObject.put("NetworkSubType", DeviceUtils.getNetworkSubType(context))
            jsonObject.put("IpAddress", DeviceUtils.getLocalIpAddress())
            jsonObject.put("userId", CloudInPreferenceManager.getString(USER_ID, ""))
            jsonObject.put("user_latitude", CloudInPreferenceManager.getString(USER_LAT, ""))
            jsonObject.put("user_longitude", CloudInPreferenceManager.getString(USER_LONG, ""))
            jsonObject.put(
                "unique_token",
                CloudInPreferenceManager.getString(USER_UNIQUE_TOKEN, "")
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override suspend fun callPost(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject
    ): Result<CommonResult> {
        addCommonDetail(jsonObject, context)
        debugInformation(urlPath, jsonObject)

        val token = "Bearer " + CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "")
        NativeUtils.ApiLog("Token ", token)
        val inputJson = JSONObject()
        inputJson.put("input", CloudInCrypto.aesEncrypt(jsonObject.toString()))
        val result: Result<CommonResult> = apiRequest {
            apiCall.commonPost(
                urlPath, inputJson.toMap(),
                token
            )
        }
        if (result is Result.Success) {
            NativeUtils.ApiLog("Encrypted Response", result.data.result!!)

            NativeUtils.ApiLog(
                "Decrypted Response",
                CloudInCrypto.aesDecrypt(result.data.result)
            )
        }
        return result
    }

    override suspend fun callPut(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject
    ): Result<CommonResult> {
        addCommonDetail(jsonObject, context)
        debugInformation(urlPath, jsonObject)

        val token = "Bearer " + CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "")
        NativeUtils.ApiLog("Token ", token)
        val inputJson = JSONObject()
        inputJson.put("input", CloudInCrypto.aesEncrypt(jsonObject.toString()))
        val result: Result<CommonResult> = apiRequest {
            apiCall.commonPut(
                urlPath, inputJson.toMap(),
                token
            )
        }
        if (result is Result.Success) {
            NativeUtils.ApiLog("Encrypted Response", result.data.result!!)

            NativeUtils.ApiLog(
                "Decrypted Response",
                CloudInCrypto.aesDecrypt(result.data.result)
            )
        }
        return result
    }

    override suspend fun callDelete(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject
    ): Result<CommonResult> {
        addCommonDetail(jsonObject, context)
        debugInformation(urlPath, jsonObject)

        val token = "Bearer " + CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "")
        NativeUtils.ApiLog("Token ", token)
        val inputJson = JSONObject()
        inputJson.put("input", CloudInCrypto.aesEncrypt(jsonObject.toString()))
        val result: Result<CommonResult> = apiRequest {
            apiCall.commonDelete(
                urlPath,
                token
            )
        }
        if (result is Result.Success) {
            NativeUtils.ApiLog("Encrypted Response", result.data.result!!)

            NativeUtils.ApiLog(
                "Decrypted Response",
                CloudInCrypto.aesDecrypt(result.data.result)
            )
        }
        return result
    }

    override suspend fun callOptions(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject
    ): Result<CommonResult> {
        addCommonDetail(jsonObject, context)
        debugInformation(urlPath, jsonObject)

        val token = "Bearer " + CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "")
        NativeUtils.ApiLog("Token ", token)
        val result: Result<CommonResult> = apiRequest {
            apiCall.commonOptions(
                urlPath, CloudInCrypto.aesEncrypt(jsonObject.toString()),
                token,
            )
        }
        if (result is Result.Success) {
            NativeUtils.ApiLog("Encrypted Response", result.data.result!!)

            NativeUtils.ApiLog(
                "Decrypted Response",
                CloudInCrypto.aesDecrypt(result.data.result)
            )
        }
        return result
    }

    override suspend fun callGet(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject
    ): Result<CommonResult> {
        addCommonDetail(jsonObject, context)
        debugInformation(urlPath, jsonObject)

        val token = "Bearer " + CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "")
        NativeUtils.ApiLog("Token ", token)
        val inputJson = JSONObject()
        inputJson.put("input", CloudInCrypto.aesEncrypt(jsonObject.toString()))
        val result: Result<CommonResult> = apiRequest {
            apiCall.commonGet(
                urlPath, CloudInCrypto.aesEncrypt(jsonObject.toString()),
                token
            )
        }
        if (result is Result.Success) {
            NativeUtils.ApiLog("Encrypted Response", result.data.result!!)

            NativeUtils.ApiLog(
                "Decrypted Response",
                CloudInCrypto.aesDecrypt(result.data.result)
            )
        }
        return result
    }

    override suspend fun callFileUpload(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject,
        imagePath: String
    ): Result<CommonResult> {
        addCommonDetail(jsonObject, context)
        debugInformation(urlPath, jsonObject)

        val token = "Bearer " + CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "")
        NativeUtils.ErrorLog("Token ", token)


        //File creating from selected URL
        val imgFile = File(imagePath)

        // create RequestBody instance from file
        val requestFile = imgFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        val body =
            MultipartBody.Part.createFormData("uploadFile", imgFile.name, requestFile)

        val result: Result<CommonResult> = apiRequest {
            apiCall.uploadImage(
                urlPath!!,
                token,
                body
            )
        }
        if (result is Result.Success) {
            NativeUtils.ApiLog("Encrypted Response", result.data.result!!)

            NativeUtils.ApiLog(
                "Decrypted Response",
                CloudInCrypto.aesDecrypt(result.data.result)
            )
        }
        return result
    }


    private fun debugInformation(urlPath: String?, jsonObject: JSONObject) {
        NativeUtils.ApiLog(
            "Decrypted URL",
            "$urlPath?$INPUT_NAME=$jsonObject"
        )
        NativeUtils.ApiLog(
            "Encrypted URL",
            urlPath + "?" + INPUT_NAME + "=" + CloudInCrypto.aesEncrypt(jsonObject.toString())
        )
    }
}
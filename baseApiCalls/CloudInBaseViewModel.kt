package com.cloudin.monsterchicken.baseApiCalls

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cloudin.monsterchicken.activity.landing.LandingActivity
import com.cloudin.monsterchicken.basedata.Result
import com.cloudin.monsterchicken.basedata.source.TaskRepository
import com.cloudin.monsterchicken.commonclass.CloudInAlerts
import com.cloudin.monsterchicken.commonclass.Loader
import com.cloudin.monsterchicken.utils.CloudInCrypto
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_AUTH_TOKEN
import com.cloudin.monsterchicken.utils.USER_ID
import com.cloudin.monsterchicken.utils.USER_IS_GOOGLE_AUTHENDICATED_USER
import com.cloudin.monsterchicken.utils.USER_NAME
import com.cloudin.monsterchicken.utils.USER_PROFILE
import com.cloudin.monsterchicken.utils.showError
import com.google.gson.Gson
import org.json.JSONObject

open class CloudInBaseViewModel(application: Application) : AndroidViewModel(application) {

    private val loaderFlagMutable = MutableLiveData<Boolean>()
    val loaderFlagLiveData: LiveData<Boolean> = loaderFlagMutable

    private val errorListMutable = MutableLiveData<List<String>>()
    val errorListLiveData: LiveData<List<String>> = errorListMutable

    val appLogout = MutableLiveData<Boolean>()
    val appLogoutLiveData: LiveData<Boolean> = appLogout

    fun errorList(errorList: List<String>?) {
        if (errorList != null) {
            errorListMutable.value = errorList!!
        }
    }

    fun showLoader() {
        loaderFlagMutable.value = true
    }

    fun hideLoader() {
        loaderFlagMutable.value = false
    }

    suspend inline fun <reified T> callPutApi(
        context: Context,
        tasksRepository: TaskRepository,
        url: String?,
        jsonObject: JSONObject,
        loading: Boolean
    ): T? {
        if (loading)
            showLoader()
        val result = tasksRepository.callPut(context, url, jsonObject)
        if (loading)
            hideLoader()

        if (result is Result.Success) {
            val decryptResult = CloudInCrypto.aesDecrypt(result.data.result)
            val resultResponse =
                NativeUtils.convertStringToPojo(decryptResult, LoginResponse::class.java)
            if (resultResponse.status) {
                return Gson().fromJson(decryptResult, T::class.java)
            } else {
                errorList(resultResponse.message)
            }

        } else if (result is Result.Error) {
            when (result.errorMessage[0]) {
                "Timeout" -> context.showError(result.errorMessage[0])
                "Job was Cancelled" -> context.showError("Timeout")
                else -> {
                    when (result.responseCode) {
                        422, 400 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                        }

                        500 -> {
                            context.showError("Internal server error")
                            NativeUtils.ErrorLog("ErrorMessage", "Internal server error")
                        }

                        405 -> {
                            context.showError("Method not found")
                            NativeUtils.ErrorLog("ErrorMessage", "Method not found")
                        }

                        401 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                            appLogout.value = true
                        }
                    }
                }
            }
        }
        return null
    }

    suspend inline fun <reified T> callDeleteApi(
        context: Context,
        tasksRepository: TaskRepository,
        url: String?,
        jsonObject: JSONObject,
        loading: Boolean
    ): T? {
        if (loading)
            showLoader()
        val result = tasksRepository.callDelete(context, url, jsonObject)
        if (loading)
            hideLoader()

        if (result is Result.Success) {
            val decryptResult = CloudInCrypto.aesDecrypt(result.data.result)
            val resultResponse =
                NativeUtils.convertStringToPojo(decryptResult, LoginResponse::class.java)
            if (resultResponse.status) {
                return Gson().fromJson(decryptResult, T::class.java)
            } else {
                errorList(resultResponse.message)
            }

        } else if (result is Result.Error) {
            when (result.errorMessage[0]) {
                "Timeout" -> context.showError(result.errorMessage[0])
                "Job was Cancelled" -> context.showError("Timeout")
                else -> {
                    when (result.responseCode) {
                        422, 400 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                        }

                        500 -> {
                            context.showError("Internal server error")
                            NativeUtils.ErrorLog("ErrorMessage", "Internal server error")
                        }

                        405 -> {
                            context.showError("Method not found")
                            NativeUtils.ErrorLog("ErrorMessage", "Method not found")
                        }

                        401 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                            appLogout.value = true
                        }
                    }
                }
            }
        }
        return null
    }

    suspend inline fun <reified T> callPostApi(
        context: Context,
        tasksRepository: TaskRepository,
        url: String?,
        jsonObject: JSONObject,
        loading: Boolean
    ): T? {
        println(jsonObject.toString())
        Log.d("check the sent json", jsonObject.toString())
        if (loading)
            showLoader()
        val result = tasksRepository.callPost(context, url, jsonObject)
        if (loading)
            hideLoader()

        if (result is Result.Success) {
            val decryptResult = CloudInCrypto.aesDecrypt(result.data.result)
            val resultResponse =
                NativeUtils.convertStringToPojo(decryptResult, LoginResponse::class.java)
            if (resultResponse.status) {
                Log.d("check", resultResponse.status.toString())
                Log.d("check1", resultResponse.toString())
                Log.d("check2", decryptResult)
                Log.d("check3", result.data.result.toString())
                return Gson().fromJson(decryptResult, T::class.java)
            } else {
                errorList(resultResponse.message)
            }

        } else if (result is Result.Error) {
            when (result.errorMessage[0]) {
                "Timeout" -> context.showError(result.errorMessage[0])
                "Job was Cancelled" -> context.showError("Timeout")
                else -> {
                    when (result.responseCode) {
                        422, 400 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                        }

                        500 -> {
                            context.showError("Internal server error")
                            NativeUtils.ErrorLog("ErrorMessage", "Internal server error")
                        }

                        405 -> {
                            context.showError("Method not found")
                            NativeUtils.ErrorLog("ErrorMessage", "Method not found")
                        }

                        401 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                            appLogout.value = true
                        }
                    }
                }
            }
        }
        return null
    }

    suspend inline fun <reified T> callGetApi(
        context: Context,
        tasksRepository: TaskRepository,
        url: String?,
        jsonObject: JSONObject,
        loading: Boolean
    ): T? {
        if (loading)
            showLoader()
        val result = tasksRepository.callGet(context, url, jsonObject)
        if (loading)
            hideLoader()

        if (result is Result.Success) {
            val decryptResult = CloudInCrypto.aesDecrypt(result.data.result)
            val resultResponse =
                NativeUtils.convertStringToPojo(decryptResult, LoginResponse::class.java)
            if (resultResponse.status) {
                return Gson().fromJson(decryptResult, T::class.java)
            } else {
                errorList(resultResponse.message)
            }

        } else if (result is Result.Error) {
            when (result.errorMessage[0]) {
                "Timeout" -> context.showError(result.errorMessage[0])
                "Job was Cancelled" -> context.showError("Timeout")
                else -> {
                    when (result.responseCode) {
                        422, 400 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                        }

                        500 -> {
                            context.showError("Internal server error")
                            NativeUtils.ErrorLog("ErrorMessage", "Internal server error")
                        }

                        405 -> {
                            context.showError("Method not found")
                            NativeUtils.ErrorLog("ErrorMessage", "Method not found")
                        }

                        401 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                            appLogout.value = true
                        }
                    }
                }
            }
        }
        return null
    }

    suspend inline fun <reified T> callOptionsApi(
        context: Context,
        tasksRepository: TaskRepository,
        url: String?,
        jsonObject: JSONObject,
        loading: Boolean
    ): T? {
        if (loading)
            showLoader()
        val result = tasksRepository.callOptions(context, url, jsonObject)
        if (loading)
            hideLoader()

        if (result is Result.Success) {
            val decryptResult = CloudInCrypto.aesDecrypt(result.data.result)
            val resultResponse =
                NativeUtils.convertStringToPojo(decryptResult, LoginResponse::class.java)
            if (resultResponse.status) {
                return Gson().fromJson(decryptResult, T::class.java)
            } else {
                errorList(resultResponse.message)
            }

        } else if (result is Result.Error) {
            when (result.errorMessage[0]) {
                "Timeout" -> context.showError(result.errorMessage[0])
                "Job was Cancelled" -> context.showError("Timeout")
                else -> {
                    when (result.responseCode) {
                        422, 400 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                        }

                        500 -> {
                            context.showError("Internal server error")
                            NativeUtils.ErrorLog("ErrorMessage", "Internal server error")
                        }

                        405 -> {
                            context.showError("Method not found")
                            NativeUtils.ErrorLog("ErrorMessage", "Method not found")
                        }

                        401 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                            appLogout.value = true
                        }
                    }
                }
            }
        }
        return null
    }

    suspend inline fun <reified T> callFileUploadApi(
        context: Context,
        tasksRepository: TaskRepository,
        url: String?,
        jsonObject: JSONObject,
        loading: Boolean,
        filePath: String
    ): T? {
        if (loading)
            showLoader()
        val result = tasksRepository.callFileUpload(context, url, jsonObject, filePath)
        if (loading)
            hideLoader()

        if (result is Result.Success) {
            val decryptResult = CloudInCrypto.aesDecrypt(result.data.result)
            val resultResponse =
                NativeUtils.convertStringToPojo(decryptResult, LoginResponse::class.java)
            if (resultResponse.status) {
                return Gson().fromJson(decryptResult, T::class.java)
            } else {
                errorList(resultResponse.message)
            }

        } else if (result is Result.Error) {
            when (result.errorMessage[0]) {
                "Timeout" -> context.showError(result.errorMessage[0])
                "Job was Cancelled" -> context.showError("Timeout")
                else -> {
                    when (result.responseCode) {
                        422, 400 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                        }
                        500 -> {
                            context.showError("Internal server error")
                            NativeUtils.ErrorLog("ErrorMessage", "Internal server error")
                        }
                        405 -> {
                            context.showError("Method not found")
                            NativeUtils.ErrorLog("ErrorMessage", "Method not found")
                        }
                        401 -> {
                            errorList(result.errorMessage)
                            NativeUtils.ErrorLog("ErrorMessage", result.errorMessage.toString())
                            appLogout.value = true
                        }
                    }
                }
            }
        }
        return null
    }
}

fun Context.errorDialog(errorList: List<String>, activity: Activity) {
    CloudInAlerts.showErrorDialog(activity, errorList)
}


fun Context.showLoader(flag: Boolean) {
    if (flag) {
        Loader.show(this)
    } else {
        Loader.hide()
    }
}

fun Context.logout(flag: Activity) {
    clearSharedPreference()
    val loginActivityIntent = Intent(this, LandingActivity::class.java)
    loginActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(loginActivityIntent)
    flag.finish()
}

fun clearSharedPreference() {
    CloudInPreferenceManager.setString(USER_ID, "")
    CloudInPreferenceManager.setString(USER_AUTH_TOKEN, "")
    CloudInPreferenceManager.setString(USER_NAME, "")
    CloudInPreferenceManager.setString(USER_PROFILE, "")
    CloudInPreferenceManager.setBoolean(USER_IS_GOOGLE_AUTHENDICATED_USER, false)
}

package com.cloudin.monsterchicken.activity.dashboard.ui.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.cart.LoginResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_LOGOUT
import com.cloudin.monsterchicken.utils.API_REQUEST_OTP_FOR_API
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_ID
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProfileViewModel(application: Application) : CloudInBaseViewModel(application) {

    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val phoneNumber = MutableLiveData<String>("")
    val otpReceived = MutableLiveData<Boolean>(false)

    fun getRequestOtp() {
        val requestOTPJSONObject = JSONObject()
        requestOTPJSONObject.put("number", phoneNumber.value)
        viewModelScope.launch {
            val loginResponse = callPostApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_REQUEST_OTP_FOR_API),
                requestOTPJSONObject,
                true
            )
            if (loginResponse != null) {
                if (loginResponse.status) {
                    CloudInPreferenceManager.setString(
                        USER_ID,
                        loginResponse.response!!.userId
                    )
                    otpReceived.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(loginResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun callLogout() {
        if (context.checkInternet()) {
            viewModelScope.launch {
                val resetPasswordResponse =
                    callPostApi<com.cloudin.monsterchicken.baseApiCalls.LoginResponse>(
                        context,
                        tasksRepository,
                        NativeUtils.getAppDomainURL(API_LOGOUT),
                        JSONObject(),
                        true
                    )
                if (resetPasswordResponse != null) {
                    if (resetPasswordResponse.status) {
                        appLogout.value = true
                    }
                }
            }
        }
    }

}
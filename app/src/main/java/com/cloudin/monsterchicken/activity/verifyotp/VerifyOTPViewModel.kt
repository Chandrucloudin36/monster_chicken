package com.cloudin.monsterchicken.activity.verifyotp

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.cart.LoginResponse
import com.cloudin.monsterchicken.activity.dashboard.ui.home.DashboardResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ADD_TO_CART
import com.cloudin.monsterchicken.utils.API_RESEND_OTP
import com.cloudin.monsterchicken.utils.API_VERIFY_OTP
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_AUTH_TOKEN
import com.cloudin.monsterchicken.utils.USER_ID
import com.cloudin.monsterchicken.utils.USER_PHONE_NUMBER
import com.cloudin.monsterchicken.utils.USER_PUSH_TOKEN
import com.cloudin.monsterchicken.utils.USER_UNIQUE_TOKEN
import kotlinx.coroutines.launch
import org.json.JSONObject

class VerifyOTPViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository

    val phoneNumber = MutableLiveData<String>("")
    val phoneNumberText = MutableLiveData<String>("")
    val otpReceived = MutableLiveData<Boolean>(false)
    val otpValidated = MutableLiveData<Boolean>(false)

    fun getResendOtp() {
        val resendOTPJSONObject = JSONObject()
        resendOTPJSONObject.put("user_id", CloudInPreferenceManager.getString(USER_ID, ""))
        viewModelScope.launch {
            val resendOtpResponse = callPostApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_RESEND_OTP),
                resendOTPJSONObject,
                true
            )
            if (resendOtpResponse != null) {
                if (resendOtpResponse.status) {
                    otpReceived.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(resendOtpResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun validateOtp(otpString: String) {
        val validateOTPJSONObject = JSONObject()
        validateOTPJSONObject.put("user_id", CloudInPreferenceManager.getString(USER_ID, ""))
        validateOTPJSONObject.put("otp", otpString)
        validateOTPJSONObject.put(
            "device_token",
            CloudInPreferenceManager.getString(USER_PUSH_TOKEN, "")
        )
        viewModelScope.launch {
            val validatedOtpResponse = callPostApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_VERIFY_OTP),
                validateOTPJSONObject,
                true
            )
            if (validatedOtpResponse != null) {
                if (validatedOtpResponse.status) {
                    CloudInPreferenceManager.setString(
                        USER_AUTH_TOKEN,
                        validatedOtpResponse.response!!.accessToken
                    )
                    CloudInPreferenceManager.setString(
                        USER_ID,
                        validatedOtpResponse.response!!.userId
                    )
                    CloudInPreferenceManager.setString(
                        USER_PHONE_NUMBER,
                        phoneNumber.value
                    )
                    updateCartDetails()
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(validatedOtpResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    private fun updateCartDetails() {
        viewModelScope.launch {
            val dashboardResponse = callGetApi<DashboardResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(
                    "$API_ADD_TO_CART?unique_token=" + CloudInPreferenceManager.getString(
                        USER_UNIQUE_TOKEN, ""
                    )
                ),
                JSONObject(),
                true
            )
            if (dashboardResponse != null) {
                if (dashboardResponse.status) {
                    otpValidated.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

}
package com.cloudin.monsterchicken.activity.profiledetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.baseApiCalls.FileUpalodResponse
import com.cloudin.monsterchicken.utils.API_GET_PROFILE_DETAILS
import com.cloudin.monsterchicken.utils.API_UPDATE_PROFILE_DETAILS
import com.cloudin.monsterchicken.utils.API_UPLOAD_DOCUMENT
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_EMAIL
import com.cloudin.monsterchicken.utils.USER_NAME
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProfileDetailsViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val userName = MutableLiveData<String>()
    val userEmail = MutableLiveData<String>()
    val userNumber = MutableLiveData<String>()
    val previewUrl = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val gender = MutableLiveData<String>()
    val genderSelected = MutableLiveData<String>()
    val isProfileUpdated = MutableLiveData<Boolean>()

    fun getProfileDetails() {
        viewModelScope.launch {
            val profileDetailsResponse = callPostApi<ProfileDetailsResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_GET_PROFILE_DETAILS),
                JSONObject(),
                true
            )
            if (profileDetailsResponse != null) {
                if (profileDetailsResponse.status) {
                    userName.value = profileDetailsResponse.response!!.userDetail.name
                    userEmail.value = profileDetailsResponse.response!!.userDetail.email
                    userNumber.value = profileDetailsResponse.response!!.userDetail.number
                    gender.value = profileDetailsResponse.response!!.userDetail.gender
                    previewUrl.value = profileDetailsResponse.response!!.userDetail.previewUrl
                    imageUrl.value = profileDetailsResponse.response!!.userDetail.image
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(profileDetailsResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun updateProfileDetails() {
        val updateProfileDetailsJSONObject = JSONObject()
        updateProfileDetailsJSONObject.put("name", userName.value)
        updateProfileDetailsJSONObject.put("email", userEmail.value)
        updateProfileDetailsJSONObject.put(
            "gender",
            genderSelected.value
        )
        updateProfileDetailsJSONObject.put(
            "number",
            userNumber.value
        )
        updateProfileDetailsJSONObject.put(
            "number",
            userNumber.value
        )
        updateProfileDetailsJSONObject.put(
            "image",
            imageUrl.value
        )
        viewModelScope.launch {
            val profileDetailsResponse = callPostApi<ProfileDetailsResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_UPDATE_PROFILE_DETAILS),
                updateProfileDetailsJSONObject,
                true
            )
            if (profileDetailsResponse != null) {
                if (profileDetailsResponse.status) {
                    CloudInPreferenceManager.setString(USER_NAME, userName.value)
                    CloudInPreferenceManager.setString(USER_EMAIL, userEmail.value)
                    isProfileUpdated.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(profileDetailsResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun updateProfileImage(filePath: String) {

        if (context.checkInternet()) {
            viewModelScope.launch {

                val fileUploadResponse =
                    callFileUploadApi<FileUpalodResponse>(
                        context,
                        tasksRepository,
                        NativeUtils.getAppDomainURL(API_UPLOAD_DOCUMENT),
                        JSONObject(),
                        true, filePath
                    )
                if (fileUploadResponse != null) {
                    if (fileUploadResponse.status) {
                        previewUrl.value = fileUploadResponse.response!!.previewUrl
                        imageUrl.value = fileUploadResponse.response!!.url
                        updateProfileDetails()
                    }
                }
            }
        }
    }
}
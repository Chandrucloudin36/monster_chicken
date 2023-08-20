package com.cloudin.monsterchicken.activity.dashboard.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_DASHBOARD
import com.cloudin.monsterchicken.utils.API_GET_UNIQUE_TOKEN
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_UNIQUE_TOKEN
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel(application: Application) : CloudInBaseViewModel(application) {

    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val categoriesList = MutableLiveData<MutableList<CategoriesList>>()
    val bannersList = MutableLiveData<MutableList<String>>()
    val isDashboardReceived = MutableLiveData<Boolean>(false)

    fun getDashboard() {
        viewModelScope.launch {
            val dashboardResponse = callGetApi<DashboardResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_DASHBOARD),
                JSONObject(),
                false
            )
            if (dashboardResponse != null) {
                if (dashboardResponse.status) {
                    categoriesList.value = mutableListOf()
                    categoriesList.value!!.clear()
                    categoriesList.value!!.addAll(dashboardResponse.response!!.categories)

                    bannersList.value = mutableListOf()
                    bannersList.value!!.clear()
                    bannersList.value!!.addAll(dashboardResponse.response!!.banners)
                    isDashboardReceived.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun getUniqueToken() {
        viewModelScope.launch {
            val dashboardResponse = callGetApi<DashboardResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_GET_UNIQUE_TOKEN),
                JSONObject(),
                false
            )
            if (dashboardResponse != null) {
                if (dashboardResponse.status) {
                    CloudInPreferenceManager.setString(
                        USER_UNIQUE_TOKEN,
                        dashboardResponse.response!!.unique_token
                    )
                    getDashboard()
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
}
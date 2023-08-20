package com.cloudin.monsterchicken

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.activity.dashboard.ui.home.DashboardResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.baseApiCalls.LoginResponse
import com.cloudin.monsterchicken.utils.API_DASHBOARD
import com.cloudin.monsterchicken.utils.NativeUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository

    fun getDashboard() {
        viewModelScope.launch {
            val stateListResponse = callPostApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_DASHBOARD),
                JSONObject(),
                true
            )
            if (stateListResponse != null) {

            }
        }
    }
}
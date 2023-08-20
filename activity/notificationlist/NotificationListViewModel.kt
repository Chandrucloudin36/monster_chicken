package com.cloudin.monsterchicken.activity.notificationlist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_GET_NOTIFICATION_LIST
import com.cloudin.monsterchicken.utils.API_UPDATE_NOTIFICATION_STATUS
import com.cloudin.monsterchicken.utils.NativeUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

class NotificationListViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val notificationListValues = MutableLiveData<MutableList<Notifications>>()
    val lastSelectedId = MutableLiveData<Int>(-1)


    fun getNotificationList() {
        viewModelScope.launch {
            val orderListResponse = callGetApi<NotificationListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_GET_NOTIFICATION_LIST),
                JSONObject(),
                true
            )
            if (orderListResponse != null) {
                if (orderListResponse.status) {
                    notificationListValues.value = mutableListOf()
                    notificationListValues.value!!.clear()
                    notificationListValues.value!!.addAll(orderListResponse.response!!.notifications)
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(orderListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun updateNotificationStatus(selectedId: Int) {
        viewModelScope.launch {
            val orderListResponse = callPostApi<NotificationListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(
                    API_UPDATE_NOTIFICATION_STATUS + notificationListValues.value!![selectedId].notification_id
                ),
                JSONObject(),
                true
            )
            if (orderListResponse != null) {
                if (orderListResponse.status) {
                    lastSelectedId.value = selectedId
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(orderListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
}
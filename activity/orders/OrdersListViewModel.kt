package com.cloudin.monsterchicken.activity.orders

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ORDER_HISTORY
import com.cloudin.monsterchicken.utils.NativeUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

class OrdersListViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val orderListValues = MutableLiveData<MutableList<OrderList>>()
    val downloadLink = MutableLiveData<OrderListResponse>()
    var orderId1: String = ""

    fun viewInvoice(orderId: String, orderNumber: String) {
        orderId1 = orderNumber
        viewModelScope.launch {
            val orderListResponse = callGetApi<OrderListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL("api/v1.0/mc-web/order/$orderId/generate-pdf"),
                JSONObject(),
                true
            )
            if (orderListResponse != null) {
                if (orderListResponse.status) {
                    downloadLink.value = orderListResponse!!
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(orderListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun getOrderList() {
        viewModelScope.launch {
            val orderListResponse = callGetApi<OrderListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_ORDER_HISTORY),
                JSONObject(),
                true
            )
            if (orderListResponse != null) {
                if (orderListResponse.status) {
                    orderListValues.value = mutableListOf()
                    orderListValues.value!!.clear()
                    orderListValues.value!!.addAll(orderListResponse.response!!.orders)
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(orderListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
}
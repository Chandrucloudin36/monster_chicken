package com.cloudin.monsterchicken.activity.dashboard.ui.home

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.checkout.CheckoutResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_DASHBOARD
import com.cloudin.monsterchicken.utils.API_GET_CART_DATA_DASHBOARD
import com.cloudin.monsterchicken.utils.API_GET_CART_SUMMARY
import com.cloudin.monsterchicken.utils.API_GET_UNIQUE_TOKEN
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_AUTH_TOKEN
import com.cloudin.monsterchicken.utils.USER_UNIQUE_TOKEN
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel(application: Application) : CloudInBaseViewModel(application) {

    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val categoriesList = MutableLiveData<MutableList<CategoriesList>>()
    val bannersList = MutableLiveData<MutableList<String>>()
    val cart_count = MutableLiveData<Int?>()
    val cart_amount = MutableLiveData<String?>()
    val isDashboardReceived = MutableLiveData<Boolean>(false)


   // fun getCartvalue(token: String) {
   fun getCartvalue() {
       val token= CloudInPreferenceManager.getString(USER_UNIQUE_TOKEN, "")
        Log.d("cartvalueeeeeee","testt")
       Log.d("cartvalueeeeeee",API_GET_CART_DATA_DASHBOARD+"?unique_token="+token)
        Log.d("cartvalueeeeeee","testt")
        viewModelScope.launch {

            val dashboardCartResponse = callGetApi<DashboardCartResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_GET_CART_DATA_DASHBOARD+"?unique_token="+token),
                JSONObject(),
                false
            )
            if (dashboardCartResponse != null) {
                if (dashboardCartResponse.status) {
                    print(dashboardCartResponse.response!!.data!!.total_cart_count)
                    cart_count.value=dashboardCartResponse!!.response!!.data!!.total_cart_count
                    cart_amount.value=dashboardCartResponse!!.response!!.data!!.total_cart_price
                   // cartCountString.value = "" + cartCount.value + "Items"

                    Log.d("dashboardCartResponse1",cart_count.value.toString())
                    Log.d("dashboardCartResponse2",cart_amount.value.toString())
                    Log.d("dashboardCartResponse3",dashboardCartResponse.toString())

                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardCartResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
    fun getDashboard() {

        Log.d("dasshhhhhvalueeeeeee","testt")
        Log.d("dasshhhhhvalueeeeeee","testt")
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

                    Log.d("dashboard1 ",dashboardResponse.toString())
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
                   // getCartvalue(dashboardResponse.response!!.unique_token)
                    getCartvalue()
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
}
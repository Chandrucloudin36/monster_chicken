package com.cloudin.monsterchicken.activity.addaddress

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.cart.LoginResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ADD_ADDRESS
import com.cloudin.monsterchicken.utils.API_GET_DISTRICT_LIST
import com.cloudin.monsterchicken.utils.API_GET_STATES_LIST
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.commondialog.CommonListResponse
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddAddressViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository

    var areaAndLocality = MutableLiveData<String>()
    var lattitude = MutableLiveData<String>()
    var longitude = MutableLiveData<String>()
    var flatNumber = MutableLiveData<String>()
    var landmark = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var pinCode = MutableLiveData<String>()
    var state = MutableLiveData<String>()
    var mobileNumber = MutableLiveData<String>()
    var nickname = MutableLiveData<String>()
    var saveAs = MutableLiveData<String>()
    var addressId = MutableLiveData<String>("")
    val addressAdded = MutableLiveData<Boolean>(false)

    var stateNameText = MutableLiveData<String>()
    var stateIdText = MutableLiveData("")

    var districtNameText = MutableLiveData<String>()
    var districtIdText = MutableLiveData<String>()

    var stateNameErrorText = MutableLiveData<String>()
    var districtNameErrorText = MutableLiveData<String>()

    var statesList: ArrayList<CommonListResponse.CommonList> = arrayListOf()
    var stateResponse = MutableLiveData<Boolean>()

    var districtList: ArrayList<CommonListResponse.CommonList> = arrayListOf()
    var districtResponse = MutableLiveData<Boolean>()

    fun addAddress(addAddress: JSONObject) {
        if (!addAddress.has("address_id"))
            viewModelScope.launch {
                val loginResponse = callPostApi<LoginResponse>(
                    context,
                    tasksRepository,
                    NativeUtils.getAppDomainURL(API_ADD_ADDRESS),
                    addAddress,
                    true
                )
                if (loginResponse != null) {
                    addressAdded.value = true
                }
            }
        else
            viewModelScope.launch {
                val loginResponse = callPutApi<LoginResponse>(
                    context,
                    tasksRepository,
                    NativeUtils.getAppDomainURL(API_ADD_ADDRESS + "/" + addAddress.getString("address_id")),
                    addAddress,
                    true
                )
                if (loginResponse != null) {
                    addressAdded.value = true
                }
            }
    }

    fun callGetStatesList() {
        if (context.checkInternet()) {
            viewModelScope.launch {

                val branchListResponse =
                    callOptionsApi<CommonListResponse>(
                        context,
                        tasksRepository,
                        NativeUtils.getAppDomainURL(API_GET_STATES_LIST),
                        JSONObject(),
                        true
                    )
                if (branchListResponse != null) {
                    if (branchListResponse.status) {
                        statesList.clear()
                        statesList.addAll(branchListResponse.response!!.common_list!!)
                        stateResponse.value = true
                    }
                }
            }
        }
    }

    fun callGetDistrictList() {
        if (context.checkInternet()) {
            viewModelScope.launch {

                val branchListResponse =
                    callOptionsApi<CommonListResponse>(
                        context,
                        tasksRepository,
                        NativeUtils.getAppDomainURL(API_GET_DISTRICT_LIST + stateIdText.value),
                        JSONObject(),
                        true
                    )
                if (branchListResponse != null) {
                    if (branchListResponse.status) {
                        districtList.clear()
                        districtList.addAll(branchListResponse.response!!.common_list!!)
                        districtResponse.value = true
                    }
                }
            }
        }
    }

}
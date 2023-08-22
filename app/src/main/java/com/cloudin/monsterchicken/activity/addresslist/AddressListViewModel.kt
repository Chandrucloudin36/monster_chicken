package com.cloudin.monsterchicken.activity.addresslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ADD_ADDRESS
import com.cloudin.monsterchicken.utils.NativeUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddressListViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val addressesListValues = MutableLiveData<MutableList<AddressesList>>()
    val lastSelectedPosition = MutableLiveData<Int>()
    val deletedPosition = MutableLiveData<Int>()
    val editAddressPosition = MutableLiveData<Int>()
    val setDefaultAddress = MutableLiveData<Boolean>(false)
    val deletedAddress = MutableLiveData<Boolean>(false)

    fun getAddressList() {
        viewModelScope.launch {
            val addressesListResponse = callGetApi<AddressListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_ADD_ADDRESS),
                JSONObject(),
                false
            )
            if (addressesListResponse != null) {
                if (addressesListResponse.status) {
                    addressesListValues.value = mutableListOf()
                    addressesListValues.value!!.clear()
                    addressesListValues.value!!.addAll(addressesListResponse.response!!.addresses)
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(addressesListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun setAddressAsDefault(setAddressDefault: String) {
        viewModelScope.launch {
            val addressesListResponse = callPutApi<AddressListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL("$API_ADD_ADDRESS/$setAddressDefault/set-default-address"),
                JSONObject(),
                false
            )
            if (addressesListResponse != null) {
                if (addressesListResponse.status) {
                    setDefaultAddress.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(addressesListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun setCallDeleteAddress(addressId: String) {
        viewModelScope.launch {
            val addressesListResponse = callDeleteApi<AddressListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL("$API_ADD_ADDRESS/$addressId"),
                JSONObject(),
                false
            )
            if (addressesListResponse != null) {
                if (addressesListResponse.status) {
                    deletedAddress.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(addressesListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun setSelectedCheckBox(position: Int) {
        lastSelectedPosition.value = position
    }

    fun setDeleteAddress(position: Int) {
        deletedPosition.value = position
    }

    fun setEditAddress(position: Int) {
        editAddressPosition.value = position
    }
}
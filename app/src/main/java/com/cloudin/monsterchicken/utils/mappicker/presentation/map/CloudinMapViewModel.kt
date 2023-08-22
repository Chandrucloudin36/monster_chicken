package com.cloudin.monsterchicken.utils.mappicker.presentation.map

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.cart.LoginResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_LOCATION_CHECK
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.mappicker.utils.SharedPrefs
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.json.JSONObject

class CloudinMapViewModel(private val sharedPrefs: SharedPrefs, application: Application) :
    CloudInBaseViewModel(application) {

    var latLngLiveData = MutableLiveData<LatLng>()

    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val isServiceAvailable = MutableLiveData<Boolean>(true)
    val isServiceAvailableMessage = MutableLiveData<String>("")

    fun fetchSavedLocation() {
        val latitude = sharedPrefs.deviceLatitude.toDouble()
        val longitude = sharedPrefs.deviceLongitude.toDouble()
        latLngLiveData.postValue(LatLng(latitude, longitude))
    }

    fun checkAddressAvailable(addAddress: JSONObject) {
        viewModelScope.launch {
            val loginResponse = callGetApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(
                    API_LOCATION_CHECK + addAddress.getString("latitude") + "&longitude=" + addAddress.getString(
                        "longitude"
                    )
                ),
                JSONObject(),
                true
            )
            if (loginResponse != null) {
                isServiceAvailableMessage.value = loginResponse.message!![0]
                isServiceAvailable.value = loginResponse.serviceProvider
            }
        }
    }

    fun saveLatLngToSharedPref(latitude: Double, longitude: Double) {
        sharedPrefs.deviceLatitude = latitude.toFloat()
        sharedPrefs.deviceLongitude = longitude.toFloat()
    }
}
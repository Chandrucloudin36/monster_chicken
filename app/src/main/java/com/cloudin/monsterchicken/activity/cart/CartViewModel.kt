package com.cloudin.monsterchicken.activity.cart

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.dashboard.ui.home.DashboardResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ADD_TO_CART
import com.cloudin.monsterchicken.utils.API_REQUEST_OTP_FOR_API
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_ID
import com.cloudin.monsterchicken.utils.USER_PUSH_TOKEN
import kotlinx.coroutines.launch
import org.json.JSONObject

class CartViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val cartListValues = MutableLiveData<MutableList<CartDetails>>()
    private val isNeedsToUpdateCartCount = MutableLiveData<Boolean>(false)
    val removeItemPosition = MutableLiveData<Boolean>(false)
    val cartListIsEmpty = MutableLiveData<Boolean>(false)
    val subTotal = MutableLiveData<String>("")
    val deliveryCharge = MutableLiveData<String>("")
    val totalBillCharge = MutableLiveData<String>("")
    val phoneNumber = MutableLiveData<String>("")
    val lastSelectedPosition = MutableLiveData<Int>()
    val otpReceived = MutableLiveData<Boolean>(false)


    fun getCartList() {
        viewModelScope.launch {
            val cartListResponse = callGetApi<CartListResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_ADD_TO_CART),
                JSONObject(),
                false
            )
            if (cartListResponse != null) {
                if (cartListResponse.status) {
                    cartListValues.value = mutableListOf()
                    cartListValues.value!!.clear()
                    cartListValues.value!!.addAll(cartListResponse.response!!.data.cart)
                    if (isNeedsToUpdateCartCount.value == true) {
                        lastSelectedPosition.value = lastSelectedPosition.value!!
                    }

                    cartListIsEmpty.value = cartListValues.value!!.size != 0

                    subTotal.value = "₹ ${cartListResponse.response!!.data.totalCartPrice}"
                    deliveryCharge.value = "₹ 0"
                    totalBillCharge.value = "₹ ${cartListResponse.response!!.data.totalCartPrice}"
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(cartListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun getRequestOtp() {
        val requestOTPJSONObject = JSONObject()
        requestOTPJSONObject.put("number", phoneNumber.value)
        requestOTPJSONObject.put(
            "device_token",
            CloudInPreferenceManager.getString(USER_PUSH_TOKEN, "")
        )
        viewModelScope.launch {
            val loginResponse = callPostApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_REQUEST_OTP_FOR_API),
                requestOTPJSONObject,
                true
            )
            if (loginResponse != null) {
                if (loginResponse.status) {
                    CloudInPreferenceManager.setString(
                        USER_ID,
                        loginResponse.response!!.userId
                    )
                    otpReceived.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(loginResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun addToCart(cartDetails: CartDetails, itemPosition: Int, isProductWantsToAdd: Boolean) {
        lastSelectedPosition.value = itemPosition
        val addProductJSONObject = JSONObject()
        if (cartDetails.quantity > 0) {
            if (isProductWantsToAdd) {
                cartDetails.quantity = cartDetails.quantity + 1
            } else {
                cartDetails.quantity = cartDetails.quantity - 1
            }
            addProductJSONObject.put("quantity", cartDetails.quantity)
        }
        addProductJSONObject.put("branch_user_id", cartDetails.branch_user.user_id)
        addProductJSONObject.put("product_id", cartDetails.product.productId)

        if (cartDetails.quantity == 0) {
            viewModelScope.launch {
                val dashboardResponse = callDeleteApi<DashboardResponse>(
                    context,
                    tasksRepository,
                    NativeUtils.getAppDomainURL("$API_ADD_TO_CART/${cartDetails.cartId}"),
                    addProductJSONObject,
                    false
                )
                if (dashboardResponse != null) {
                    if (dashboardResponse.status) {
                        isNeedsToUpdateCartCount.value = true
                        getCartList()
                    } else {
                        val errorList: MutableList<String> = ArrayList()
                        errorList.addAll(dashboardResponse.message!!)
                        errorList(errorList)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                val dashboardResponse = callPostApi<DashboardResponse>(
                    context,
                    tasksRepository,
                    NativeUtils.getAppDomainURL(API_ADD_TO_CART),
                    addProductJSONObject,
                    false
                )
                if (dashboardResponse != null) {
                    if (dashboardResponse.status) {
                        removeItemPosition.value = cartDetails.quantity == 0
                        isNeedsToUpdateCartCount.value = true
                        getCartList()
                    } else {
                        val errorList: MutableList<String> = ArrayList()
                        errorList.addAll(dashboardResponse.message!!)
                        errorList(errorList)
                    }
                }
            }
        }
    }
}
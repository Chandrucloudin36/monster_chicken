package com.cloudin.monsterchicken.activity.productdetatails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.dashboard.ui.home.DashboardResponse
import com.cloudin.monsterchicken.activity.dashboard.ui.home.ImageUrl
import com.cloudin.monsterchicken.activity.dashboard.ui.home.ProductList
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ADD_TO_CART
import com.cloudin.monsterchicken.utils.API_PRODUCT_DETAILS
import com.cloudin.monsterchicken.utils.NativeUtils
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProductDetailsViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository

    val productId = MutableLiveData<String>("")
    val nearByBranch = MutableLiveData<String>("")
    val brand = MutableLiveData<String>("")
    val description = MutableLiveData<String>("")
    val productName = MutableLiveData<String>("")
    val productUnit = MutableLiveData<String>("")
    val productUnitType = MutableLiveData<String>("")
    val price = MutableLiveData<String>("")
    val productImageList = MutableLiveData<List<ImageUrl>>()
    val productDetails = MutableLiveData<ProductList>()

    val selectedCategoryName = MutableLiveData<String>()

    fun getProductDetails() {
        val productListJSONObject = JSONObject()
        viewModelScope.launch {
            val productDetailsResponse = callGetApi<ProductDetailsResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_PRODUCT_DETAILS + productId.value + "/" + nearByBranch.value),
                productListJSONObject,
                false
            )
            if (productDetailsResponse != null) {
                if (productDetailsResponse.status) {
                    productDetails.value = productDetailsResponse.response!!.productDetail[0]
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(productDetailsResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun addToCart(isProductWantsToAdd: Boolean) {

        val addProductJSONObject = JSONObject()
        if (isProductWantsToAdd) {
            productDetails.value!!.cartProductQuantity =
                productDetails.value!!.cartProductQuantity!! + 1
        } else {
            productDetails.value!!.cartProductQuantity =
                productDetails.value!!.cartProductQuantity!! - 1
        }
        addProductJSONObject.put("quantity", productDetails.value!!.cartProductQuantity!!)
        addProductJSONObject.put("branch_user_id", productDetails.value!!.nearByBranch)
        addProductJSONObject.put("product_id", productDetails.value!!.productId)

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
                    getProductDetails()
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
}
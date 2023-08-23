package com.cloudin.monsterchicken.activity.productlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.dashboard.ui.home.CartLists
import com.cloudin.monsterchicken.activity.dashboard.ui.home.CategoriesList
import com.cloudin.monsterchicken.activity.dashboard.ui.home.DashboardCartResponse
import com.cloudin.monsterchicken.activity.dashboard.ui.home.DashboardResponse
import com.cloudin.monsterchicken.activity.dashboard.ui.home.ProductList
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ADD_TO_CART
import com.cloudin.monsterchicken.utils.API_GET_CART_DATA_DASHBOARD
import com.cloudin.monsterchicken.utils.API_GET_PRODUCT_LIST
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_UNIQUE_TOKEN
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProductListViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val selectedCategoryId = MutableLiveData<String>()
    val selectedCategoryName = MutableLiveData<String>()
    val productCount = MutableLiveData<String>()
    val lastSelectedPosition = MutableLiveData<Int>()
    val cartCount = MutableLiveData<Int?>()
    val cartTotalPrice = MutableLiveData<String?>()
    val cartCountString = MutableLiveData<String>()
    private val isNeedsToUpdateCartCount = MutableLiveData<Boolean>(false)
    val categoriesList = MutableLiveData<MutableList<CategoriesList>>()
    val productItemList = MutableLiveData<MutableList<ProductList>>()
    private val cartItemList = MutableLiveData<MutableList<CartLists>>()


    fun getCartvalue() {
        val token= CloudInPreferenceManager.getString(USER_UNIQUE_TOKEN, "")
        Log.d("cartvalueeeeeee","testt")
        Log.d("cartvalueeeeeee", API_GET_CART_DATA_DASHBOARD +"?unique_token="+token)
        Log.d("cartvalueeeeeee","testt")
        viewModelScope.launch {

            val dashboardCartResponse = callGetApi<DashboardCartResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_GET_CART_DATA_DASHBOARD +"?unique_token="+token),
                JSONObject(),
                false
            )
            if (dashboardCartResponse != null) {
                if (dashboardCartResponse.status) {
                    print(dashboardCartResponse.response!!.data!!.total_cart_count)
                    cartCount.value=dashboardCartResponse!!.response!!.data!!.total_cart_count
                    cartTotalPrice.value=dashboardCartResponse!!.response!!.data!!.total_cart_price
                    cartCountString.value = "" + cartCount.value + "Items"
                    // cartCountString.value = "" + cartCount.value + "Items"

                    Log.d("dashboardCartResponse1",cartCount.value.toString())
                    Log.d("dashboardCartResponse2",cartTotalPrice.value.toString())
                    Log.d("dashboardCartResponse3",dashboardCartResponse.toString())

                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardCartResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
    fun getProductList() {
        val productListJSONObject = JSONObject()
        productListJSONObject.put("product_category_id", selectedCategoryId.value)
        viewModelScope.launch {
            val dashboardResponse = callGetApi<DashboardResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_GET_PRODUCT_LIST),
                productListJSONObject,
                false
            )
            if (dashboardResponse != null) {
                if (dashboardResponse.status) {

                    productItemList.value = mutableListOf()
                    productItemList.value!!.clear()
                    productItemList.value!!.addAll(dashboardResponse.response!!.products)
                    if (isNeedsToUpdateCartCount.value == true) {
                        productItemList.value!![lastSelectedPosition.value!!].showProgress = false
                        lastSelectedPosition.value = lastSelectedPosition.value!!
                        isNeedsToUpdateCartCount.value = false
                    } else {
                        categoriesList.value = mutableListOf()
                        categoriesList.value!!.clear()
                        categoriesList.value!!.addAll(dashboardResponse.response!!.categories)

                        val productCountInt = productItemList.value!!.size
                        productCount.value = "$productCountInt items"
                    }
                    cartCount.value = dashboardResponse.response!!.cart_details.cart_count
                    cartTotalPrice.value =
                        dashboardResponse.response!!.cart_details.cart_total_amount
                    cartCountString.value = "" + cartCount.value + "Items"

                    if (cartCount.value!! > 0) {
                        cartItemList.value = mutableListOf()
                        cartItemList.value!!.clear()
                        cartItemList.value!!.addAll(dashboardResponse.response!!.cart_details.cart_lists)
                    }
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(dashboardResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun addToCart(productListItem: ProductList, itemPosition: Int, isProductWantsToAdd: Boolean) {
        productItemList.value!![itemPosition].showProgress = true
        lastSelectedPosition.value = itemPosition

        val addProductJSONObject = JSONObject()
        if (isProductWantsToAdd) {
            productListItem.cartProductQuantity = productListItem.cartProductQuantity!! + 1
        } else {
            productListItem.cartProductQuantity = productListItem.cartProductQuantity!! - 1
        }
        addProductJSONObject.put("quantity", productListItem.cartProductQuantity!!)
        addProductJSONObject.put("branch_user_id", productListItem.nearByBranch)
        addProductJSONObject.put("product_id", productListItem.productId)

        if (productListItem.cartProductQuantity!! > 0)
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
                        isNeedsToUpdateCartCount.value = true
                        getProductList()
                     //  getCartvalue()
                    } else {
                        val errorList: MutableList<String> = ArrayList()
                        errorList.addAll(dashboardResponse.message!!)
                        errorList(errorList)
                    }
                }
            }
        else {
            var cartId = ""

            if (cartItemList.value!!.size > 0) {

                for (cartList in cartItemList.value!!)
                    if (cartList.productDetails.productId!! == productListItem.productId) {
                        cartId = cartList.cart_id
                        continue
                    }

                if (cartId != "")
                    viewModelScope.launch {
                        val dashboardResponse = callDeleteApi<DashboardResponse>(
                            context,
                            tasksRepository,
                            NativeUtils.getAppDomainURL("$API_ADD_TO_CART/$cartId"),
                            addProductJSONObject,
                            false
                        )
                        if (dashboardResponse != null) {
                            if (dashboardResponse.status) {
                                isNeedsToUpdateCartCount.value = true
                                getProductList()
                                //getCartvalue()
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

}
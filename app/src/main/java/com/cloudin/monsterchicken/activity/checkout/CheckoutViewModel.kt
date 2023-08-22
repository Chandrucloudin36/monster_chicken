package com.cloudin.monsterchicken.activity.checkout

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cloudin.monsterchicken.TaskApplication
import com.cloudin.monsterchicken.activity.addresslist.AddressListResponse
import com.cloudin.monsterchicken.activity.addresslist.AddressesList
import com.cloudin.monsterchicken.activity.cart.LoginResponse
import com.cloudin.monsterchicken.baseApiCalls.CloudInBaseViewModel
import com.cloudin.monsterchicken.utils.API_ADD_ADDRESS
import com.cloudin.monsterchicken.utils.API_CHECK_PROMO_CODE
import com.cloudin.monsterchicken.utils.API_GET_CART_SUMMARY
import com.cloudin.monsterchicken.utils.API_PAYMENT_UPDATE
import com.cloudin.monsterchicken.utils.API_UPDATE_PAYMENT_STATUS
import com.cloudin.monsterchicken.utils.NativeUtils
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CheckoutViewModel(application: Application) : CloudInBaseViewModel(application) {
    val context = application as TaskApplication
    private val tasksRepository = (context).taskRepository
    val checkoutListValues = MutableLiveData<MutableList<CartDetails>>()

    var cartItemList: ArrayList<String> = arrayListOf()

    val subTotal = MutableLiveData<String>("")
    val deliveryCharge = MutableLiveData<String>("")
    val discountAmount = MutableLiveData<String>("")
    val discountAmountString = MutableLiveData<String>("")
    val totalBillCharge = MutableLiveData<String>("")
    val addressType = MutableLiveData<String>("")
    val addressId = MutableLiveData<String>("")
    val addressArea = MutableLiveData<String>("")
    val appliedCoupon = MutableLiveData<String>("")
    private val couponId = MutableLiveData<String>("")
    val isExistAddress = MutableLiveData<Boolean>(false)
    val isOrderConfirmed = MutableLiveData<Boolean>()

    val addressesListValues = MutableLiveData<MutableList<AddressesList>>()
    val isDefaultAddressSelected = MutableLiveData<Boolean>(false)
    val addressListReceived = MutableLiveData<Boolean>()

    fun getCartData() {
        viewModelScope.launch {
            val cartDataJSONObject = JSONObject()
            val cartItemId = JSONArray()

            for (cartItem in cartItemList)
                cartItemId.put(cartItem)

            cartDataJSONObject.put("cartId", cartItemId)

            val cartListResponse = callGetApi<CheckoutResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_GET_CART_SUMMARY),
                JSONObject(),
                true
            )
            if (cartListResponse != null) {
                if (cartListResponse.status) {
                    checkoutListValues.value = mutableListOf()
                    checkoutListValues.value!!.clear()
                    checkoutListValues.value!!.addAll(cartListResponse.response!!.cartData)

                    subTotal.value = "₹ ${cartListResponse.response!!.grandTotal}"
                    deliveryCharge.value = "₹ ${cartListResponse.response!!.deliveryCharge}"
                    discountAmount.value = "₹ ${cartListResponse.response!!.discountPrice}"
                    totalBillCharge.value =
                        "₹ ${cartListResponse.response!!.grandTotal}"

                    discountAmountString.value = cartListResponse.response!!.discountPrice!!

                    if (cartListResponse.response!!.isAddressAvailable != null) {
                        addressId.value =
                            cartListResponse.response!!.isAddressAvailable!!.address_id
                        addressArea.value =
                            cartListResponse.response!!.isAddressAvailable!!.area

                        if (cartListResponse.response!!.isAddressAvailable!!.type == "home")
                            addressType.value = "Home"
                        else if (cartListResponse.response!!.isAddressAvailable!!.type == "work")
                            addressType.value = "Work"
                        else if (cartListResponse.response!!.isAddressAvailable!!.type == "others")
                            addressType.value =
                                cartListResponse.response!!.isAddressAvailable!!.nick_name
                        isExistAddress.value = true
                    } else
                        isExistAddress.value = false

                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(cartListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }


    private fun callPaymentConfirmation(orderId: String) {
        viewModelScope.launch {
            val cartDataJSONObject = JSONObject()
            cartDataJSONObject.put("method", "cash_on_delivery")
            cartDataJSONObject.put("status", "COD")
            cartDataJSONObject.put("order_id", orderId)
            cartDataJSONObject.put(
                "amount", subTotal.value!!.replace(
                    "₹ ",
                    ""
                )
            )

            val checkoutDataResponse = callPostApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_UPDATE_PAYMENT_STATUS + orderId),
                cartDataJSONObject,
                true
            )

            if (checkoutDataResponse != null) {
                if (checkoutDataResponse.status) {
                    isOrderConfirmed.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(checkoutDataResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun callPlaceOrder(date: String,from_time: String,to_time: String) {
        viewModelScope.launch {
            val cartDataJSONObject = JSONObject()
           cartDataJSONObject.put("promo_code_id", couponId.value)
            cartDataJSONObject.put("schedule_date", date)
            cartDataJSONObject.put("schedule_from_time", from_time)
            cartDataJSONObject.put("schedule_to_time", to_time)
           // val times = timeSlotView.text.split(" - ")

          /*  if (couponId.value.equals(""))
                if(couponId.value.isNullOrEmpty()){
                    cartDataJSONObject.put("promo_code_id", couponId.value)
                }else{
                    cartDataJSONObject.put("promo_code_id", couponId.value)
                }*/
               // cartDataJSONObject.put("promo_code_id", couponId.value.toString())
           // couponId.value?.let { cartDataJSONObject.put("promo_code_id", couponId.value.toString()) }
            couponId.value?.let { Log.d("****promo_code_id*****", it) }
            Log.d("cartdata", cartDataJSONObject.toString())
            Log.d("Tag", "The coupon is: $couponId.value")

            val checkoutDataResponse = callPostApi<LoginResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(API_PAYMENT_UPDATE),
                cartDataJSONObject,
                true
            )

            if (checkoutDataResponse != null) {
                if (checkoutDataResponse.status) {
                    callPaymentConfirmation(checkoutDataResponse.orderId)
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(checkoutDataResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

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
                    addressListReceived.value = true
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(addressesListResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }

    fun applyOffer() {
        viewModelScope.launch {
            val offerApplyResponse = callGetApi<OfferApplyResponse>(
                context,
                tasksRepository,
                NativeUtils.getAppDomainURL(
                    API_CHECK_PROMO_CODE + appliedCoupon.value + "&sub_total=" + subTotal.value!!.replace(
                        "₹ ",
                        ""
                    )
                ),
                JSONObject(),
                true
            )
            if (offerApplyResponse != null) {
                if (offerApplyResponse.status && offerApplyResponse.offer != null) {
                    if (offerApplyResponse.offer!!.cart_discount_amount > 0) {
                        discountAmount.value =
                            "₹ ${offerApplyResponse.offer!!.cart_discount_amount}"

                        discountAmountString.value =
                            offerApplyResponse.offer!!.cart_discount_amount.toString()

                        val subTotal: String = subTotal.value!!.replace("₹ ", "")
                        val totalValue =
                            subTotal.toInt() - offerApplyResponse.offer!!.cart_discount_amount

                        totalBillCharge.value = "₹ $totalValue"
                        couponId.value = offerApplyResponse.offer!!.offer_id
                    }
                } else {
                    val errorList: MutableList<String> = ArrayList()
                    errorList.addAll(offerApplyResponse.message!!)
                    errorList(errorList)
                }
            }
        }
    }
}
package com.cloudin.monsterchicken.activity.cart

import android.os.Parcelable
import com.cloudin.monsterchicken.activity.dashboard.ui.home.ProductList
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartListResponse(
    @SerializedName("message")
    @Expose
    var message: List<String>? = null,

    @SerializedName("response")
    @Expose
    var response: Response? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean = false
) : Parcelable

@Parcelize
data class Response(
    @SerializedName("data")
    @Expose
    var data: DataResponse,
) : Parcelable

@Parcelize
data class DataResponse(
    @SerializedName("carts")
    @Expose
    var cart: List<CartDetails> = arrayListOf(),
    @SerializedName("deliveryCharge")
    @Expose
    var deliveryCharge: String = "",
    @SerializedName("total_cart_price")
    @Expose
    var totalCartPrice: String = "",
    @SerializedName("grandTotal")
    @Expose
    var grandTotal: String = "",
) : Parcelable

@Parcelize
data class CartDetails(
    @SerializedName("cart_id")
    @Expose
    var cartId: String = "",
    @SerializedName("productId")
    @Expose
    var productId: String = "",
    @SerializedName("nearByBranch")
    @Expose
    var nearByBranch: String = "",
    @SerializedName("maxQuantity")
    @Expose
    var maxQuantity: Int = 0,
    @SerializedName("quantity")
    @Expose
    var quantity: Int = 0,
    @SerializedName("stock")
    @Expose
    var stock: Int = 0,
    @SerializedName("productName")
    @Expose
    var productName: String = "",
    @SerializedName("price")
    @Expose
    var price: String = "",
    @SerializedName("unit")
    @Expose
    var unit: String = "",
    @SerializedName("unitType")
    @Expose
    var unitType: String = "",
    @SerializedName("total_amount")
    @Expose
    var totalPrice: String = "",
    @SerializedName("productCategory")
    @Expose
    var productCategory: String = "",
    @SerializedName("product")
    @Expose
    var product: ProductList,
    @SerializedName("branch_user")
    @Expose
    var branch_user: BranchUser,
) : Parcelable


@Parcelize
data class BranchUser(
    @SerializedName("user_id")
    @Expose
    var user_id: String,
) : Parcelable
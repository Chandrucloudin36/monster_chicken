package com.cloudin.monsterchicken.activity.checkout

import android.os.Parcelable
import com.cloudin.monsterchicken.activity.dashboard.ui.home.McUnit
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckoutResponse(
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
    @SerializedName("cart_data")
    @Expose
    var cartData: List<CartDetails> = arrayListOf(),
    @SerializedName("is_address_available")
    @Expose
    var isAddressAvailable: IsAddressAvailable? = null,
    @SerializedName("delivery_charge")
    @Expose
    var deliveryCharge: String? = "",
    @SerializedName("discountPrice")
    @Expose
    var discountPrice: String? = "",
    @SerializedName("totalCartPrice")
    @Expose
    var totalCartPrice: String? = "",
    @SerializedName("grand_total")
    @Expose
    var grandTotal: String? = "",
) : Parcelable

@Parcelize
data class IsAddressAvailable(
    @SerializedName("isExistAddress")
    @Expose
    var isExistAddress: Boolean = false,
    @SerializedName("address_id")
    @Expose
    var address_id: String,
    @SerializedName("area")
    @Expose
    var area: String,
    @SerializedName("type")
    @Expose
    var type: String,
    @SerializedName("nick_name")
    @Expose
    var nick_name: String,
    @SerializedName("address")
    @Expose
    var address: Address? = null,
) : Parcelable

@Parcelize
data class Address(
    @SerializedName("id")
    @Expose
    var id: String? = "",
    @SerializedName("area")
    @Expose
    var area: String? = "",
    @SerializedName("type")
    @Expose
    var type: String? = "",
    @SerializedName("nickName")
    @Expose
    var nickName: String? = "",
) : Parcelable


@Parcelize
data class CartDetails(
    @SerializedName("cart_id")
    @Expose
    var cartId: String = "",
    @SerializedName("productId")
    @Expose
    var productId: String = "",
    @SerializedName("maxQuantity")
    @Expose
    var maxQuantity: Int = 0,
    @SerializedName("quantity")
    @Expose
    var quantity: Int = 0,
    @SerializedName("productName")
    @Expose
    var productName: String = "",
    @SerializedName("price")
    @Expose
    var price: String = "",
    @SerializedName("unit")
    @Expose
    var unit: String = "",
    @SerializedName("total_price")
    @Expose
    var totalPrice: String = "",
    @SerializedName("productCategory")
    @Expose
    var productCategory: String = "",
    @SerializedName("product")
    @Expose
    var product: Product,
) : Parcelable

@Parcelize
data class Product(
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("mc_unit")
    @Expose
    var mc_unit: McUnit? = null,
) : Parcelable
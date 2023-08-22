package com.cloudin.monsterchicken.activity.orders

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderListResponse(
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
    @SerializedName("orders")
    @Expose
    var orders: List<OrderList> = arrayListOf(),
    @SerializedName("download_url")
    @Expose
    var download_url: String = "",
) : Parcelable

@Parcelize
data class OrderList(
    @SerializedName("order_id")
    @Expose
    var order_id: String = "",
    @SerializedName("order_number")
    @Expose
    var order_number: String = "",
    @SerializedName("total_amount")
    @Expose
    var total_amount: String = "",
    @SerializedName("created_at")
    @Expose
    var created_at: String = "",
    @SerializedName("status")
    @Expose
    var status: Status,
    @SerializedName("branch")
    @Expose
    var branch: Branch,
    @SerializedName("order_items")
    @Expose
    var products: List<ProductsItem> = arrayListOf(),
) : Parcelable

@Parcelize
data class ProductsItem(
    @SerializedName("quantity")
    @Expose
    var quantity: String,
    @SerializedName("product")
    @Expose
    var product: Product,
) : Parcelable

@Parcelize
data class Product(
    @SerializedName("name")
    @Expose
    var name: String,
) : Parcelable

@Parcelize
data class Status(
    @SerializedName("name")
    @Expose
    var name: String,
) : Parcelable

@Parcelize
data class Branch(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("address")
    @Expose
    var address: List<Address>,
) : Parcelable

@Parcelize
data class Address(
    @SerializedName("city")
    @Expose
    var city: String,
) : Parcelable
package com.cloudin.monsterchicken.activity.dashboard.ui.home

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DashboardCartResponse(
    @SerializedName("message")
    @Expose
    var message: List<String>? = null,

    @SerializedName("response")
    @Expose
    var response: Response1? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean = false
) : Parcelable

@Parcelize
data class Response1(

    @SerializedName("data")
    @Expose
    var data: data1? = null,
   /* @SerializedName("data")
    @Expose
    var CartData: List<CartData> = arrayListOf(),*/
) : Parcelable


@Parcelize
data class data1(

    @SerializedName("total_cart_price")
    @Expose
    var total_cart_price: String? = null,

    @SerializedName("total_cart_count")
    @Expose
    var total_cart_count: Int? = null,
    /* @SerializedName("data")
     @Expose
     var CartData: List<CartData> = arrayListOf(),*/
) : Parcelable


/*@Parcelize
data class CartData(
    @SerializedName("carts")
    @Expose
    var CartDetailData: List<CartDetailData> = arrayListOf(),

    @SerializedName("total_cart_price")
    @Expose
    var total_cart_price: String,
    @SerializedName("total_cart_count")
    @Expose
    var total_cart_count: String,
) : Parcelable

@Parcelize
data class CartDetailData(
    *//*@SerializedName("carts")
    @Expose
    var CartDetailData: List<CartData> = arrayListOf(),*//*

    @SerializedName("total_cart_price")
    @Expose
    var total_cart_price: String,
    @SerializedName("total_cart_count")
    @Expose
    var total_cart_count: String,
) : Parcelable*/




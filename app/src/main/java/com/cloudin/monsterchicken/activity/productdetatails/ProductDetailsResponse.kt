package com.cloudin.monsterchicken.activity.productdetatails

import android.os.Parcelable
import com.cloudin.monsterchicken.activity.dashboard.ui.home.ProductList
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ProductDetailsResponse(
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
    @SerializedName("products")
    @Expose
    var productDetail: List<ProductList>,
) : Parcelable

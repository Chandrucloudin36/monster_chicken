package com.cloudin.monsterchicken.activity.cart

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class LoginResponse(
    @SerializedName("message")
    @Expose
    var message: List<String>? = null,

    @SerializedName("response")
    @Expose
    var response: ResponseLogin? = null,

    @SerializedName("serviceProvider")
    @Expose
    var serviceProvider: Boolean = false,

    @SerializedName("orderId")
    @Expose
    var orderId: String = "",

    @SerializedName("status")
    @Expose
    var status: Boolean = false


) : Parcelable

@Parcelize
data class ResponseLogin(
    @SerializedName("user_id", alternate = ["userId"])
    @Expose
    var userId: String = "",
    @SerializedName("orderId")
    @Expose
    var orderId: String = "",
    @SerializedName("addressId")
    @Expose
    var addressId: String = "",
    @SerializedName("access_token")
    @Expose
    var accessToken: String = "",
) : Parcelable
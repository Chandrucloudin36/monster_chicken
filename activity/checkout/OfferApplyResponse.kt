package com.cloudin.monsterchicken.activity.checkout

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OfferApplyResponse(
    @SerializedName("message")
    @Expose
    var message: List<String>? = null,

    @SerializedName("offer")
    @Expose
    var offer: Offer? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean = false
) : Parcelable

@Parcelize
data class Offer(
    @SerializedName("offer_id")
    @Expose
    var offer_id: String,
    @SerializedName("cart_discount_amount")
    @Expose
    var cart_discount_amount: Int = 0,
) : Parcelable
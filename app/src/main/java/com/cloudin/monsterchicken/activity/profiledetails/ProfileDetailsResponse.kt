package com.cloudin.monsterchicken.activity.profiledetails

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileDetailsResponse(
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
    @SerializedName("userDetail")
    @Expose
    var userDetail: UserDetails,
) : Parcelable

@Parcelize
data class UserDetails(
    @SerializedName("name")
    @Expose
    var name: String = "",
    @SerializedName("email")
    @Expose
    var email: String = "",
    @SerializedName("number")
    @Expose
    var number: String = "",
    @SerializedName("image")
    @Expose
    var image: String = "",
    @SerializedName("previewUrl")
    @Expose
    var previewUrl: String = "",
    @SerializedName("gender")
    @Expose
    var gender: String = "",
) : Parcelable
package com.cloudin.monsterchicken.activity.addresslist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data
class AddressListResponse(
    @SerializedName("message")
    @Expose
    var message: List<String>? = null,

    @SerializedName("response")
    @Expose
    var response: Response? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean = false
) : Serializable

data class Response(
    @SerializedName("addresses")
    @Expose
    var addresses: List<AddressesList> = arrayListOf(),
) : Serializable

data class AddressesList(
    @SerializedName("address_id")
    @Expose
    var id: String = "",
    @SerializedName("city")
    @Expose
    var city: String = "",
    @SerializedName("area")
    @Expose
    var area: String = "",
    @SerializedName("latitude")
    @Expose
    var latitude: String = "",
    @SerializedName("longitude")
    @Expose
    var longitude: String = "",
    @SerializedName("landmark")
    @Expose
    var landMark: String = "",
    @SerializedName("street_name")
    @Expose
    var streetName: String = "",
    @SerializedName("number")
    @Expose
    var number: String = "",
    @SerializedName("type")
    @Expose
    var type: String = "",
    @SerializedName("others")
    @Expose
    var others: String = "",
    @SerializedName("pincode")
    @Expose
    var pincode: String = "",
    @SerializedName("nick_name")
    @Expose
    var nickName: String = "",
    @SerializedName("default_address")
    @Expose
    var defaultAddress: Int,
    @SerializedName("state")
    @Expose
    var state: State,
    @SerializedName("district")
    @Expose
    var district: State,
) : Serializable

data class State(
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("state", alternate = ["district"])
    @Expose
    var state: String,
) : Serializable

data class City(
    @SerializedName("state")
    @Expose
    var name: String = "",
) : Serializable

package com.cloudin.monsterchicken.basedata

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonResult {
    @Expose
    @SerializedName("data")
    val result: String? = null

    @SerializedName("statusCode")
    @Expose
    val statusCode: String? = null

    @SerializedName("statusMessage")
    @Expose
    val statusMessage: String? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("error")
    @Expose
    private val error: Boolean? = null

    fun getError(): Boolean? {
        return error
    }
}

class Data {
    @SerializedName("response")
    @Expose
    var response: String? = null
}
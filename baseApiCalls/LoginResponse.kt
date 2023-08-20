package com.cloudin.monsterchicken.baseApiCalls

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("message")
    @Expose
    var message: List<String>? = null

    @SerializedName("response")
    @Expose
    var response: Response? = null

    @SerializedName("status")
    @Expose
    var status: Boolean = false

    class Response {
        @SerializedName("accessToken")
        @Expose
        var access_token: String? = null

        @SerializedName("userId")
        @Expose
        var user_id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("onHold")
        @Expose
        var onHold: Boolean? = null

        @SerializedName("permanentId")
        @Expose
        var permanentId: String? = null
    }

}
package com.cloudin.monsterchicken.baseApiCalls

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class FileUpalodResponse {
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
        @SerializedName("url")
        @Expose
        var url: String? = ""

        @SerializedName("previewUrl")
        @Expose
        var previewUrl: String? = ""
    }
}
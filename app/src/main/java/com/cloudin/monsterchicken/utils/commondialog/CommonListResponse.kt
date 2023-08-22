package com.cloudin.monsterchicken.utils.commondialog

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommonListResponse {
    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("response")
    @Expose
    var response: Response? = null

    @SerializedName("message")
    @Expose
    var message: List<String>? = null

    class Response {

        @SerializedName(
            "states",
            alternate = ["districts"]
        )
        @Expose
        var common_list: List<CommonList>? = null
    }

    class CommonList {
        @SerializedName("value", alternate = ["branchId"])
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null
    }
}
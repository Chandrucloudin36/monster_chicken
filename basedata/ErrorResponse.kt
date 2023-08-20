package com.cloudin.monsterchicken.basedata

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message") val errorResponseList: List<String>
)
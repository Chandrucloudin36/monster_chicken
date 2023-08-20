package com.cloudin.monsterchicken.basedata.source

import android.content.Context
import com.cloudin.monsterchicken.basedata.CommonResult
import com.cloudin.monsterchicken.basedata.Result
import org.json.JSONObject

interface TaskRepository {

    suspend fun callPost(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject,
    ): Result<CommonResult>

    suspend fun callPut(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject,
    ): Result<CommonResult>

    suspend fun callDelete(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject,
    ): Result<CommonResult>

    suspend fun callOptions(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject,
    ): Result<CommonResult>

    suspend fun callGet(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject,
    ): Result<CommonResult>

    suspend fun callFileUpload(
        context: Context,
        urlPath: String?,
        jsonObject: JSONObject,
        imagePath: String
    ): Result<CommonResult>

}
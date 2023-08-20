package com.cloudin.monsterchicken.basedata

sealed class Result<out R> {

    data class Success<out T>(val responseCode: Int, val data: T) : Result<T>()
    data class Error(val responseCode: Int, val exception: String, val errorMessage: List<String>) :
        Result<Nothing>()

    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null
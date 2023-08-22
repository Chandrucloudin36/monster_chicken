package com.cloudin.monsterchicken.utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import java.util.regex.Pattern

inline fun <reified R> PojoConvert(data: String?): R {
    return Gson().fromJson(data, R::class.java)
}

fun ErrorValidation(context: Context, error: String) {
    NativeUtils.ErrorLog("Toast", error)
    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
}

fun Context.showError(error: String) {
    NativeUtils.ErrorLog("Toast", error)
    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
}


fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.checkValidatePassword(): Boolean {
    val passwordPatterns = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"
    return !TextUtils.isEmpty(this) && Pattern.matches(this, passwordPatterns)
}

fun String.isValidAccountNumber(): Boolean {
    val accountNumberPattern: String? = "^\\d{9,18}\$"
    return !TextUtils.isEmpty(this) && Pattern.matches(this, accountNumberPattern!!)
}

fun String.isValidIfsc(): Boolean {
    val ifscCodePattern: String? = "^[A-Z]{4}0[A-Z0-9]{6}\$"
    return !TextUtils.isEmpty(this) && Pattern.matches(this, ifscCodePattern!!)
}

fun String.getNameAlias(): String {
    return if (this.trim().split(" ").size > 1)
        this.trim().split(" ")[0][0].toString() + this.trim().split(" ")[1][0].toString()
    else
        this.trim().split(" ")[0][0].toString()
}

fun String.toIntConvert(): Int {
    val value: Int = this.toIntOrNull() ?: return 0
    return value
}

fun EditText.onDone(callback: () -> Unit) {
    maxLines = 1
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        }
        false
    }
}




package com.cloudin.monsterchicken.utils.mappicker.extenstion

fun String?.isRequiredField(): Boolean {
    return this != null && isNotEmpty() && isNotBlank()
}


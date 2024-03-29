package com.cloudin.monsterchicken.utils.mappicker.utils

import android.widget.Toast

sealed class ToastLength(val value: Int) {
    object Short : ToastLength(Toast.LENGTH_SHORT)
    object Long : ToastLength(Toast.LENGTH_LONG)
}
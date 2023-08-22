package com.cloudin.monsterchicken.utils

import android.content.DialogInterface

interface NativeDialogClickListener {
    fun onPositive(dialog: DialogInterface?)

    fun onNegative(dialog: DialogInterface?)
}
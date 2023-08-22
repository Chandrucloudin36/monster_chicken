package com.cloudin.monsterchicken.utils

import com.cloudin.monsterchicken.BuildConfig

class Constant {
    companion object {
        val debug: Boolean = BuildConfig.DEBUG
        const val IS_PRODUCTION = false
        const val flavour = "test"
        const val flavourName = "com.cloudin.monsterchicken.test"
    }
}
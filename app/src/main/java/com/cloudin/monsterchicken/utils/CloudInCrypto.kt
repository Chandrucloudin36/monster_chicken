package com.cloudin.monsterchicken.utils

import android.util.Base64.DEFAULT
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object CloudInCrypto {
    @JvmStatic
    fun aesEncrypt(v: String?) = AES256.encrypt(v)

    @JvmStatic
    fun aesDecrypt(v: String?) = AES256.decrypt(v)

    init {
        System.loadLibrary("cloudin-jni")
    }

}

private object AES256 {
    external fun getPasswordKey(): String?
    external fun getIVKey(): String?

    private fun cipher(opMode: Int): Cipher {
        val ivKey = IvParameterSpec(getIVKey()!!.toByteArray())
        val secretKey = SecretKeySpec(getPasswordKey()!!.toByteArray(), "AES")
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        c.init(opMode, secretKey, ivKey)
        return c
    }

    fun encrypt(str: String?): String {
        val encrypted = cipher(Cipher.ENCRYPT_MODE).doFinal(str!!.toByteArray(Charsets.UTF_8))
        return android.util.Base64.encodeToString(encrypted, DEFAULT)
    }

    fun decrypt(str: String?): String {
        if (str != null && str != "") {
            val byteStr = android.util.Base64.decode(str, DEFAULT)
            return String(cipher(Cipher.DECRYPT_MODE).doFinal(byteStr))
        }
        return ""
    }
}
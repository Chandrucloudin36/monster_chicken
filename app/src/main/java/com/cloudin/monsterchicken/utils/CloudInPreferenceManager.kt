package com.cloudin.monsterchicken.utils

import android.content.SharedPreferences

class CloudInPreferenceManager {

    companion object {

        private var sharedPreferences: SharedPreferences? = null

        fun initializePreferenceManager(preferences: SharedPreferences?) {
            sharedPreferences = preferences
        }

        /**
         * @param key
         * @param defaultValue
         * @return value store in key
         */
        fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
            return sharedPreferences!!.getBoolean(key, defaultValue)
        }

        /**
         * @param key
         * @param value
         */
        fun setBoolean(key: String?, value: Boolean) {
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(key, value)
            editor.commit()
        }

        /**
         * @param key
         * @param defaultValue
         * @return value store in key
         */
        fun getLong(key: String?, defaultValue: Long): Long? {
            return sharedPreferences!!.getLong(key, defaultValue)
        }

        /**
         * @param key
         * @param value
         */
        fun setLong(key: String?, value: Long) {
            val editor = sharedPreferences!!.edit()
            editor.putLong(key, value)
            editor.commit()
        }

        /**
         * @return
         */
        fun getUserId(): String? {
            return sharedPreferences!!.getString(USER_ID, "")
        }

        /**
         * @param key
         * @param defaultValue
         * @return
         */
        fun getString(key: String?, defaultValue: String?): String? {
            if (sharedPreferences != null)
                return sharedPreferences!!.getString(key, defaultValue)
            return ""
        }

        /**
         * @param key
         * @param value
         */
        fun setString(key: String?, value: String?) {
            val editor = sharedPreferences!!.edit()
            if (value == null) editor.putString(key, "").apply() else editor.putString(
                key,
                value
            ).apply()
        }


        /**
         * @param key
         * @param defaultValue
         * @return
         */
        fun getInteger(key: String?, defaultValue: Int): Int {
            return sharedPreferences!!.getInt(key, defaultValue)
        }

        /**
         * @param key
         * @param value
         */
        fun setInteger(key: String?, value: Int) {
            val editor = sharedPreferences!!.edit()
            editor.putInt(key, value).apply()
        }

        /**
         * to clear all the values stored in shared proference
         */
        fun clearShdPref() {
            sharedPreferences!!.edit().clear().apply()
        }

    }
}
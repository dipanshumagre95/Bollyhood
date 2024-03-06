package com.app.bollyhood.util

import android.content.Context
import android.content.SharedPreferences

class PrefManager(val context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var PRIVATE_MODE = 0

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    var loginId: String?
        get() = pref.getString(LOGIN_ID, "0")
        set(id) {
            editor.putString(LOGIN_ID, id)
            editor.commit()
        }
    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    fun setvalue(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getvalue(key: String?, defaultvalue: Boolean?): Boolean {
        return pref.getBoolean(key, defaultvalue!!)
    }

    fun setvalue(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun getvalue(key: String?, defaultvalue: Int): Int {
        return pref.getInt(key, defaultvalue)
    }

    fun setvalue(key: String?, value: String?) {
        editor.putString(key, value)
        editor.commit()
    }

    fun getvalue(key: String?): String? {
        return pref.getString(key, "0")
    }

    fun getStringValue(key: String?): String? {
        return pref.getString(key, "")
    }

    fun clearValue() {
        pref.edit().clear().commit()
    }

    companion object {
        private const val PREF_NAME = "UserData"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        private const val LOGIN_ID = "LOGIN"

    }
}
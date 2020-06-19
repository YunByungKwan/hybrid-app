package com.example.hybridapp.util.module

import android.content.Context
import com.example.hybridapp.App

object SharedPreferences {
    
    /** 데이터 저장 */
    fun putData(fileName: String, key: String, value: Any?) {
        val prefs
                = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        when (value) {
            is Boolean -> {
                editor.putBoolean(key, value).apply()
            }
            is Float -> {
                editor.putFloat(key, value).apply()
            }
            is Int -> {
                editor.putInt(key, value).apply()
            }
            is Long -> {
                editor.putLong(key, value).apply()
            }
            is String -> {
                editor.putString(key, value).apply()
            }
            else -> {
                editor.putStringSet(key, value as Set<String>).apply()
            }
        }
    }

    /** 데이터 불러오기 */
    fun getBoolean(fileName: String, key: String): Boolean {
        val prefs
                = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getBoolean(key, false) ?: false
    }

    fun getFloat(fileName: String, key: String): Float {
        val prefs
                = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getFloat(key, 0F) ?: 0F

    }

    fun getInt(fileName: String, key: String): Int {
        val prefs
                = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getInt(key, 0) ?: 0
    }

    fun getLong(fileName: String, key: String): Long {
        val prefs
                = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return prefs?.getLong(key, 0) ?: 0
    }

    fun getString(fileName: String, key: String): String {
        val prefs
                = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)

        return if(prefs != null) prefs.getString(key, "")!! else ""
    }

    /** 데이터 제거 */
    fun removeData(fileName: String, key: String) {
        val prefs = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.remove(key).apply()
    }
}
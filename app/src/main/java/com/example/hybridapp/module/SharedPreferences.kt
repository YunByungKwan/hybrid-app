package com.example.hybridapp.module

import android.content.Context
import android.util.Log
import com.example.hybridapp.App
import com.example.hybridapp.util.AndroidKeyStoreUtil
import com.example.hybridapp.util.Utils
import java.lang.IllegalArgumentException

object SharedPreferences {

    /** 데이터 저장 */
    fun putData(fileName: String, key: String, value: Any?) {
        val prefs = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        when (value) {
            is Boolean, is Int, is Long, is Float, is String -> putInternalValue(fileName, key, value)
            // TODO : Set 인 경우 값 하나하나 암호화 처리 필요
            else -> editor.putStringSet(key, value as Set<String>).apply()
        }
    }

    /** 내부에 value 암호화해서 저장 */
    private fun putInternalValue(fileName: String, key: String, value: Any) {
        val prefs = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        try {
            editor.run {
                if(value == null) {
                    remove(key)
                } else {
                    val secureValue = AndroidKeyStoreUtil.encrypt(value.toString())
                    putString(key, secureValue)
                    Log.d("dlgodnjs", "$value changed : $secureValue is putValue")
                }

                apply()
            }
        }
        catch (e: Throwable) {
            Utils.LOGD("[SharedPreferences] Data put 진행 중 오류 발생.")
            e.printStackTrace()
        }
    }


    /** 내부에서 value 불러와서 복호화 */
    private fun getInternalValue(fileName: String, key: String, defaultValue: Any) : Any? {
        val pref = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val beforeValue = pref.getString(key, "")

        if(beforeValue.isNullOrEmpty()) {
            return defaultValue
        }

        val value = AndroidKeyStoreUtil.decrypt(beforeValue)
        Log.d("dlgodnjs", "$beforeValue is changed : $value is getValue")
        return when (defaultValue) {
            is Boolean -> value.toBoolean()
            is Int -> value.toInt()
            is Float -> value.toFloat()
            is Long -> value.toLong()
            is String -> value
            else -> throw IllegalArgumentException("defaultValue only could be one of these types: Boolean, Int, Long, String")
        }
    }

    /** 데이터 불러오기
     *  하나로 합칠 수 없다. (Any 가 리턴일 경우 일일히 type을 cast 해야하기 때문에
     * */
    fun get(fileName: String, key: String, defaultValue: Boolean) : Boolean =
        getInternalValue(fileName, key, defaultValue) as Boolean

    fun get(fileName: String, key: String, defaultValue: String) : String =
        getInternalValue(fileName, key, defaultValue) as String

    fun get(fileName: String, key: String, defaultValue: Float) : Float =
        getInternalValue(fileName, key, defaultValue) as Float

    fun get(fileName: String, key: String, defaultValue: Int) : Int =
        getInternalValue(fileName, key, defaultValue) as Int

    fun get(fileName: String, key: String, defaultValue: Long) : Long =
        getInternalValue(fileName, key, defaultValue) as Long


    /** 데이터 제거 */
    fun removeData(fileName: String, key: String) {
        val prefs = (App.activity).getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.remove(key).apply()
    }
}
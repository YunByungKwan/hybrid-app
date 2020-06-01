package com.example.hybridapp.util.module

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

/**
 * activeNetworkInfo is deprecated in API level 29(Q)
 *
 * activeNetwork is added in API level 23(M)
 * getNetworkCapabilities() is added in API level 21(L)
 * hasTransport() is added in API level 21(L)
 */

object Network {

    /** 네트워크 연결 체크 */
    fun isConnected(): Boolean {
        Constants.LOGE("isConnected", Constants.TAG_NETWORK)

        val connectivityManger
                = App.activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(Constants.TAG_NETWORK, "Build version is greater than Marshmallow.")

            val activeNetwork = connectivityManger.activeNetwork ?: return false
            val networkCapabilities
                    = connectivityManger.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.e(Constants.TAG_NETWORK, "")

                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            Log.e(Constants.TAG_NETWORK, "Build version is smaller than Marshmallow.")

            connectivityManger.activeNetworkInfo ?: return false
        }

        return true
    }

    /**
     * 네트워크 상태를 확인
     *
     * return :
     * 0 : 연결되지 않음
     * 1 : 데이터 연결됨
     * 2 : 와이파이 연결됨
     */
    fun getStatus(): Int {
        Constants.LOGE("getStatus", Constants.TAG_NETWORK)

        val connectivityManger
                = App.activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(Constants.TAG_NETWORK, "Build version is greater than Marshmallow.")

            val activeNetwork = connectivityManger.activeNetwork ?: return 0
            val networkCapabilities
                    = connectivityManger.getNetworkCapabilities(activeNetwork) ?: return 0
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 1
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 2
                else -> 0
            }
        } else {
            Log.e(Constants.TAG_NETWORK, "Build version is smaller than Marshmallow.")

            connectivityManger.activeNetworkInfo ?: return 0
        }

        return 0
    }
}
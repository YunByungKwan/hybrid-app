package com.example.hybridapp.module

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils

/**
 * activeNetworkInfo is deprecated in API level 29(Q)
 *
 * activeNetwork is added in API level 23(M)
 * getNetworkCapabilities() is added in API level 21(L)
 * hasTransport() is added in API level 21(L)
 */

object Network {

    /**
     * 네트워크 상태를 확인
     *
     * Return :
     * 0 : 연결되지 않음
     * 1 : 데이터 연결됨
     * 2 : 와이파이 연결됨
     */
    fun getStatus(context: Context): Int {
        val manager = getConnectivityManager(context)
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getNetworkStatusMorMore(manager)
        } else {
            getNetworkStatusLessThanM(manager)
        }
    }

    /** M보다 크거나 같을 경우 네트워크 상태 반환 */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkStatusMorMore(manager: ConnectivityManager): Int {
        val network = manager.activeNetwork ?: return Constants.NET_STAT_DISCONNECTED
        val capabilities = manager.getNetworkCapabilities(network) ?: return Constants.NET_STAT_DISCONNECTED
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Constants.NET_STAT_CELLULAR
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Constants.NET_STAT_WIFI
            }
            else -> {
                Constants.NET_STAT_DISCONNECTED
            }
        }
    }

    /** M보다 작을 경우 네트워크 상태 반환 */
    private fun getNetworkStatusLessThanM(manager: ConnectivityManager): Int {
        val network = manager.activeNetworkInfo
            ?: return Constants.NET_STAT_DISCONNECTED
        return when(network.type) {
            ConnectivityManager.TYPE_MOBILE -> {
                Constants.NET_STAT_CELLULAR
            }
            ConnectivityManager.TYPE_WIFI -> {
                Constants.NET_STAT_WIFI
            }
            else -> {
                Constants.NET_STAT_DISCONNECTED
            }
        }
    }

    /** ConnectivityManager 생성 */
    private fun getConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}
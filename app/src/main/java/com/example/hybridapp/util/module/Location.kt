package com.example.hybridapp.util.module

import android.location.Location
import android.util.Log
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.google.android.gms.location.LocationServices

object Location {

    fun getCurrent(action: FlexAction?) {
        val client = LocationServices
            .getFusedLocationProviderClient(App.activity)

        val currentLocation: MutableMap<String, Double> = mutableMapOf()

        try {
            client!!.lastLocation!!.addOnSuccessListener(App.activity) { location : Location? ->
                if(location == null) {
                    Log.e("LOG", "location is null")
                } else location.apply {
                    Log.e("LOG", "${this.latitude}, ${this.longitude}")

                    currentLocation["latitude"] = this.latitude
                    currentLocation["longitude"] = this.longitude

                    action?.promiseReturn(currentLocation)
                }
            }
            client.lastLocation!!.addOnCanceledListener {
                action?.promiseReturn(null)
            }
            client.lastLocation!!.addOnCompleteListener {
                action?.promiseReturn(null)
            }
            client.lastLocation!!.addOnFailureListener {
                action?.promiseReturn(null)
            }
        } catch (e: NullPointerException) {
            Log.e(Constants.TAG_MAIN, e.stackTrace.toString())
            action?.promiseReturn(null)
        }
    }
}
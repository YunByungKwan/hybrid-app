package com.example.hybridapp

import android.app.NativeActivity
import android.content.Intent

class SecureActivity: NativeActivity() {

    fun failJob() {

    }

    fun successJob() {
        startActivity(Intent(this,SplashActivity::class.java))
    }

}
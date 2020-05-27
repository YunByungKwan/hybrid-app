package com.example.hybridapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.example.hybridapp.util.sms.SMSReceiver

class App : Application() {

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }

    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        super.registerActivityLifecycleCallbacks(callback)
    }

    inner class MyActivityLifecycleCallbacks: ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        }

        override fun onActivityResumed(_activity: Activity) {
            activity = _activity
        }
    }

    companion object {
        lateinit var INSTANCE: App
        lateinit var activity: Activity
        lateinit var smsReceiver: SMSReceiver

        fun context() : Context {
            return INSTANCE.applicationContext
        }
    }
}
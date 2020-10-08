package com.example.hybridapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

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

        override fun onActivityCreated(_activity: Activity, savedInstanceState: Bundle?) {
            activity = _activity
        }

        override fun onActivityResumed(_activity: Activity) {

        }
    }

    companion object {
        lateinit var INSTANCE: App
        lateinit var activity: Activity

        fun context(): Context =  INSTANCE.applicationContext
    }
}
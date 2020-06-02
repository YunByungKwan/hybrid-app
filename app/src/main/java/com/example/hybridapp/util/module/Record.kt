package com.example.hybridapp.util.module

import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils

object Record {

    private val basicActivity = App.activity as BasicActivity

    /** Get intent */
    fun getIntent(action: FlexAction?) {
        val packageManager = App.INSTANCE.packageManager
        basicActivity.recordAction = action

        val recordIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)

        if(Utils.existsReceiveActivity(recordIntent, packageManager)) {
            basicActivity.cameraAction = action
            basicActivity.startActivityForResult(recordIntent, Constants.REQ_CODE_RECORD)
        } else {
            Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_CAMERA)
            action?.promiseReturn(null)
        }
    }
}
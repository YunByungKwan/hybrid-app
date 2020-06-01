package com.example.hybridapp.util.module

import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.CenterActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.util.base.BaseActivity

object Camera {

    /** 카메라 호출 */
    fun request(action: FlexAction) {
        Constants.LOGE("request", Constants.TAG_CAMERA)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = App.INSTANCE.packageManager
        val baseActivity = BaseActivity()

        baseActivity.cameraAction = action

        if(Utils().existsReceiveActivity(cameraIntent, packageManager)) {
            baseActivity.startActivityForResult(cameraIntent, Constants.REQ_CODE_CAMERA)
        } else {
            Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_CAMERA)
        }
    }
}
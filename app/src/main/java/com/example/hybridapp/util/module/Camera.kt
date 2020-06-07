package com.example.hybridapp.util.module

import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.example.hybridapp.basic.BasicActivity

object Camera {

    /** 카메라 호출 */
    fun request(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?) {
        Constants.LOGD("Call request() in Camera object.")

        if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA)) && action != null) {
            Constants.LOGD("Camera permission exists.")

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val packageManager = App.INSTANCE.packageManager
            val basicActivity = App.activity as BasicActivity
            basicActivity.ratio = ratio

            // 카메라 앱이 있는지 확인
            if(Utils.existsReceiveActivity(cameraIntent, packageManager)) {
                if(isWidthRatio != null) { // 디바이스 기준으로 resize
                    basicActivity.cameraDeviceAction = action
                    basicActivity.isWidthRatio = isWidthRatio

                    basicActivity.startActivityForResult(cameraIntent,
                        Constants.REQ_CODE_CAMERA_DEVICE_RATIO)
                } else { // 이미지 기준으로 resize
                    basicActivity.cameraAction = action

                    basicActivity.startActivityForResult(cameraIntent,
                        Constants.REQ_CODE_CAMERA_RATIO)
                }
            } else {
                Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_CAMERA)
                action.promiseReturn(null)
            }
        } else { // 권한이 없을 경우
            Utils.checkDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                Constants.REQ_PERM_CODE_CAMERA)
            action?.promiseReturn(null)
        }
    }
}
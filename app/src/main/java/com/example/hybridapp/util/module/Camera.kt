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
        Constants.LOGD("Call request()")

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
                        Constants.CAMERA_DEVICE_RATIO_REQ_CODE)
                } else { // 이미지 기준으로 resize
                    basicActivity.cameraAction = action

                    basicActivity.startActivityForResult(cameraIntent,
                        Constants.CAMERA_RATIO_REQ_CODE)
                }
            } else {
                Constants.LOGE(Constants.LOG_MSG_CAMERA)
                action.promiseReturn(null)
            }
        } else { // 권한이 없을 경우
            Utils.checkDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                Constants.PERM_CAMERA_REQ_CODE)
            action?.promiseReturn(null)
        }
    }
}
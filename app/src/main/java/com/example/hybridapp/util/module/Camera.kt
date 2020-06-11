package com.example.hybridapp.util.module

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider.getUriForFile
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import java.io.File


object Camera {

    /** 카메라 호출 */
    fun request(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?) {
        Constants.LOGD("Call request()")

        if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA)) && action != null) {
            Constants.LOGD("Camera permission exists.")

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(Utils.getOutputMediaFile() == null) {
                return action.resolveVoid()
            }

            // 카메라 촬영 시 저장할 임시 파일 경로 생성 및 인텐트에 적용
            val newFile= Utils.getOutputMediaFile()!!
            val contentUri = getUriForFile(App.context(), Utils.getAppId() + ".fileprovider", newFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)

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
                action.resolveVoid()
            }
        } else { // 권한이 없을 경우
            action?.resolveVoid()
            Utils.checkDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                Constants.PERM_CAMERA_REQ_CODE)
        }
    }
}
package com.example.hybridapp.util.module

import android.content.Intent
import android.provider.MediaStore
import androidx.core.content.FileProvider.getUriForFile
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.firebase.BuildConfig


object Camera {

    /** 카메라 호출
     * CameraByDeviceRatio, CameraByRatio에서 호출
     */
    fun request() {
        Utils.LOGD("Call request() in Camera object.")

        val basicActivity = App.activity as BasicActivity
        val packageManager = (App.INSTANCE).packageManager
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 카메라 촬영 시 저장할 임시 파일 경로 생성 및 인텐트에 적용
        val newFile= Utils.getOutputMediaFile()!!
        val contentUri = getUriForFile(App.context(), BuildConfig.APPLICATION_ID + ".fileprovider", newFile)
//        val filePath = Photo.getFilePathFromUri(contentUri)
//        val degree = Photo.getDegreesFromPath(filePath)
//        Utils.LOGD("Degree: ${degree}")
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)

        // 카메라 앱을 사용할 수 있는 경우
        if(Utils.existsReceiveActivity(cameraIntent, packageManager)) {
            // 디바이스 기준으로 resize
            if(basicActivity.isWidthRatio != null) {
                basicActivity.startActivityForResult(cameraIntent,
                    Constants.CAMERA_DEVICE_RATIO_REQ_CODE)
            }
            // 이미지 기준으로 resize
            else {
                basicActivity.startActivityForResult(cameraIntent,
                    Constants.CAMERA_RATIO_REQ_CODE)
            }
        }
        // 카메라 앱을 사용할 수 없는 경우
        else {
            val returnObj = Utils.createJSONObject(true,
                null, App.context().getString(R.string.msg_not_load_camera))

            // 디바이스 기준일 경우
            if(basicActivity.isWidthRatio != null) {
                basicActivity.cameraDeviceAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
                basicActivity.isWidthRatio = null
            }
            // 이미지 기준일 경우
            else {
                basicActivity.cameraAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
            }
        }
    }
}
package com.example.hybridapp.util.module

import android.content.Intent
import android.provider.MediaStore
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils


object Camera {

    /** 카메라 호출
     * CameraByDeviceRatio, CameraByRatio에서 호출
     */
    fun request(isWidthRatio: Boolean?) {
        Constants.LOGD("Call request() in Camera object.")

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val basicActivity = App.activity as BasicActivity
        val packageManager = (App.INSTANCE).packageManager

        // 카메라 앱을 사용할 수 있는 경우
        if(Utils.existsReceiveActivity(cameraIntent, packageManager)) {
            // 디바이스 기준으로 resize
            if(isWidthRatio != null) {
                basicActivity.isWidthRatio = isWidthRatio
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
            val returnObj = Utils.createJSONObject(
                true, Constants.MSG_NOT_LOAD_CAMERA)
            basicActivity.cameraDeviceAction?.promiseReturn(returnObj)
        }
    }
}
package com.example.hybridapp.module

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.content.FileProvider.getUriForFile
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Utils
import com.google.firebase.BuildConfig
import kotlinx.android.synthetic.main.activity_main.*


class Camera {

    private val basicActivity = App.activity as BasicActivity
    private val deniedObj = Utils.createJSONObject(false,
        null, basicActivity.getString(R.string.msg_denied_perm))

    /** onRequestPermissionResult */
    val requestPermissionResultByDevice = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.CAMERA
    ) { isGranted ->
        if(isGranted) {
            basicActivity.camera!!.request()
        } else {
            basicActivity.cameraDeviceAction?.promiseReturn(deniedObj)
            basicActivity.ratio = null
            basicActivity.isWidthRatio = null
        }
    }

    val requestPermission = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.CAMERA
    ) { isGranted ->
        if(isGranted) {
            basicActivity.camera!!.request()
        } else {
            basicActivity.cameraAction?.promiseReturn(deniedObj)
            basicActivity.ratio = null
        }
    }

    /** onActivityResult */
    private val activityResultForDevice = BasicActivity().registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

        // Constants.CAMERA_DEVICE_RATIO_REQ_CODE
        Utils.LOGD("CAMERA DEVICE RATIO in onActivityResult()")
        Utils.visibleProgressBar()
        // 뒷배경 뷰 생성
        val mInflater = Utils.getLayoutInflater(App.activity)
        BasicActivity().backgroundView = mInflater.inflate(R.layout.background_popup, BasicActivity().constraintLayout)
        App.activity.constraintLayout.addView(BasicActivity().backgroundView)

        // 카메라 촬영 성공
        if(result.resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(Utils.getOutputMediaFile()!!))
            }
            else {
                MediaStore.Images.Media.getBitmap(BasicActivity().contentResolver, Uri.fromFile(Utils.getOutputMediaFile()))
            }
            val degree = basicActivity.photo!!.getDegreesFromPath(Utils.getOutputMediaFile().toString())
            Utils.LOGD("Degree: $degree")

            val rotatedBitmap = basicActivity.photo!!.rotateBitmap(bitmap, degree)
            val resizedBitmap = basicActivity.photo!!.resizeBitmapByDeviceRatio(rotatedBitmap!!,
                BasicActivity().ratio!!, BasicActivity().isWidthRatio)
            if(bitmap != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) +
                        basicActivity.photo!!.getBase64FromBitmap(resizedBitmap)
                val returnObj = Utils.createJSONObject(true,
                    base64, null)
                BasicActivity().cameraDeviceAction?.promiseReturn(returnObj)
                BasicActivity().ratio = null
                BasicActivity().isWidthRatio = null
            } else {
                Utils.LOGE("사진이 존재하지 않습니다")
                val returnObj = Utils.createJSONObject(true,
                    null, "사진이 존재하지 않습니다")
                BasicActivity().cameraDeviceAction?.promiseReturn(returnObj)
                BasicActivity().ratio = null
                BasicActivity().isWidthRatio = null
            }
        }
        // 카메라 촬영 실패
        else {
            Utils.LOGE("취소되었습니다")
            val returnObj = Utils.createJSONObject(true,
                null, "취소되었습니다")
            BasicActivity().cameraDeviceAction?.promiseReturn(returnObj)
            BasicActivity().ratio = null
            BasicActivity().isWidthRatio = null
        }

        BasicActivity().constraintLayout.removeView(BasicActivity().backgroundView)
        Utils.invisibleProgressBar()
    }

    private val activityResult = BasicActivity().registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        // 카메라 촬영 성공
        if (result.resultCode == Activity.RESULT_OK) {
            Utils.visibleProgressBar()
            val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(Utils.getOutputMediaFile()!!))
            } else {
                MediaStore.Images.Media.getBitmap(
                    App.INSTANCE.contentResolver,
                    Uri.fromFile(Utils.getOutputMediaFile())
                )
            }
            Utils.invisibleProgressBar()

            if (bitmap != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) +
                        basicActivity.photo!!.getBase64FromBitmap(bitmap)
                val returnObj = Utils.createJSONObject(
                    true,
                    base64, null
                )
                basicActivity.cameraAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
            } else {
                Utils.LOGE("사진이 존재하지 않습니다")
                val returnObj = Utils.createJSONObject(
                    true,
                    null, "사진이 존재하지 않습니다"
                )
                basicActivity.cameraAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
            }
        }
        // 카메라 촬영 실패
        else {
            Utils.LOGE("취소되었습니다")
            val returnObj = Utils.createJSONObject(
                true,
                null, "취소되었습니다"
            )
            basicActivity.cameraAction?.promiseReturn(returnObj)
            basicActivity.ratio = null
        }

        basicActivity.cameraAction?.resolveVoid()
    }

    /** 카메라 호출
     * CameraByDeviceRatio, CameraByRatio에서 호출
     */
    fun request() {
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

        if(Utils.existsReceiveActivity(cameraIntent, packageManager)) {
            if(basicActivity.isWidthRatio != null) {
                activityResultForDevice.launch(cameraIntent)
            }else {
                activityResult.launch(cameraIntent)
            }
        } else {
            val returnObj = Utils.createJSONObject(true,
                null, App.context().getString(R.string.msg_not_load_camera))

            if(basicActivity.isWidthRatio != null) {
                basicActivity.cameraDeviceAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
                basicActivity.isWidthRatio = null
            } else {
                basicActivity.cameraAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
            }
        }
    }
}
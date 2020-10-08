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
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import androidx.core.content.FileProvider.getUriForFile
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import com.google.firebase.BuildConfig
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Camera(private val basicActivity: BasicActivity) {

    var cameraDeviceAction: FlexAction? = null
    var cameraAction: FlexAction? = null
    var ratio: Double? = null
    var isWidthRatio: Boolean? = null

    /**====================================== Action =============================================*/

    val actionByDeviceRatio
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            cameraDeviceAction = action
            ratio = array[0].reified()
            isWidthRatio = array[1].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                takePicture()
            } else {
                requestPermissionResultByDevice.launch()
            }
        }
    }

    /** onRequestPermissionResult */
    private val requestPermissionResultByDevice
            = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.CAMERA) { isGranted ->
        if(isGranted) {
            takePicture()
        } else {
            val deniedObj = Utils.createJSONObject(false,
                null, basicActivity.getString(R.string.msg_denied_perm))
            cameraDeviceAction?.promiseReturn(deniedObj)
            ratio = null
            isWidthRatio = null
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
            val degree = basicActivity.photoInstance!!.getDegreesFromPath(Utils.getOutputMediaFile().toString())
            Utils.LOGD("Degree: $degree")

            val rotatedBitmap = basicActivity.photoInstance!!.rotateBitmap(bitmap, degree)
            val resizedBitmap = basicActivity.photoInstance!!.resizeBitmapByDeviceRatio(rotatedBitmap!!,
                ratio!!, isWidthRatio)
            if(bitmap != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) +
                        basicActivity.photoInstance!!.getBase64FromBitmap(resizedBitmap)
                val returnObj = Utils.createJSONObject(true,
                    base64, null)
                cameraDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            } else {
                Utils.LOGE("사진이 존재하지 않습니다")
                val returnObj = Utils.createJSONObject(true,
                    null, "사진이 존재하지 않습니다")
                cameraDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
        }
        // 카메라 촬영 실패
        else {
            Utils.LOGE("취소되었습니다")
            val returnObj = Utils.createJSONObject(true,
                null, "취소되었습니다")
            cameraDeviceAction?.promiseReturn(returnObj)
            ratio = null
            isWidthRatio = null
        }

        BasicActivity().constraintLayout.removeView(BasicActivity().backgroundView)
        Utils.invisibleProgressBar()
    }

    val actionByRatio
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            cameraAction = action
            ratio = array[0].reified()

            if(Utils.existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                takePicture()
            } else {
                requestPermissionResult.launch()
            }
        }
    }

    /** onRequestPermissionResult */
    private val requestPermissionResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), Manifest.permission.CAMERA
    ) { isGranted ->
        if(isGranted) {
            takePicture()
        } else {
            val deniedObj = Utils.createJSONObject(false,
                null, basicActivity.getString(R.string.msg_denied_perm))
            cameraAction?.promiseReturn(deniedObj)
            ratio = null
        }
    }

    /** onActivityResult */
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
                        basicActivity.photoInstance!!.getBase64FromBitmap(bitmap)
                val returnObj = Utils.createJSONObject(
                    true,
                    base64, null
                )
                cameraAction?.promiseReturn(returnObj)
                ratio = null
            } else {
                Utils.LOGE("사진이 존재하지 않습니다")
                val returnObj = Utils.createJSONObject(
                    true,
                    null, "사진이 존재하지 않습니다"
                )
                cameraAction?.promiseReturn(returnObj)
                ratio = null
            }
        }
        // 카메라 촬영 실패
        else {
            Utils.LOGE("취소되었습니다")
            val returnObj = Utils.createJSONObject(
                true,
                null, "취소되었습니다"
            )
            cameraAction?.promiseReturn(returnObj)
            ratio = null
        }

        cameraAction?.resolveVoid()
    }

    /** 카메라 호출 */
    private fun takePicture() {
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
            if(isWidthRatio != null) {
                activityResultForDevice.launch(cameraIntent)
            }else {
                activityResult.launch(cameraIntent)
            }
        } else {
            val returnObj = Utils.createJSONObject(true,
                null, App.context().getString(R.string.msg_not_load_camera))

            if(isWidthRatio != null) {
                cameraDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            } else {
                cameraAction?.promiseReturn(returnObj)
                ratio = null
            }
        }
    }
}
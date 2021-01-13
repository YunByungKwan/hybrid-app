package com.example.hybridapp.module

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider.getUriForFile
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.MainActivity
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraCompat(private val basicAct: MainActivity) {

    var cameraDeviceAction: FlexAction? = null
    var cameraAction: FlexAction? = null
    var ratio: Double? = null
    var isWidthRatio: Boolean? = null
    var currentPhotoPath: String? = null

    /** 카메라 호출 */
    private fun dispatchTakePictureIntent() {
        val packageManager = (App.INSTANCE).packageManager
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 카메라 촬영 시 저장할 임시 파일 경로 생성 및 인텐트에 적용
        val newFile: File? = try {
            createImageFile()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        newFile?.also {
            val contentUri = getUriForFile(basicAct, "com.example.hybridapp.fileprovider", newFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)

            if(Utils.existsReceiveActivity(cameraIntent, packageManager)) {
                if(isWidthRatio != null) {
                    activityResultForDevice.launch(cameraIntent)
                }else {
                    activityResult.launch(cameraIntent)
                }
            } else {
                val returnObj = Utils.returnJson(true,
                    null, App.context.getString(R.string.msg_not_load_camera))

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

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = App.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    /**====================================== Action =============================================*/

    val actionByDeviceRatio
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            cameraDeviceAction = action
            ratio = array[0].reified()
            isWidthRatio = array[1].reified()
            val permissions = arrayOf(Constants.PERM_CAMERA, Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(permissions)) {
                dispatchTakePictureIntent()
            } else {
                permissionResultByDevice.launch(permissions)
            }
        }
    }

    /** onRequestPermissionResult */
    private val permissionResultByDevice
            = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if(Utils.existAllPermission(permissions)) {
            dispatchTakePictureIntent()
        } else {
            val deniedObj = Utils.returnJson(false, null, basicAct.getString(R.string.msg_denied_perm))
            cameraDeviceAction?.promiseReturn(deniedObj)
            ratio = null
            isWidthRatio = null
        }
    }

    /** onActivityResult */
    private val activityResultForDevice = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if(Utils.resultOk(result.resultCode)) {
            val file = File(currentPhotoPath!!)
            val bitmap: Bitmap? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(basicAct.contentResolver, Uri.fromFile(file))
                ImageDecoder.decodeBitmap(source)
            }
            else {
                MediaStore.Images.Media.getBitmap(basicAct.contentResolver, Uri.fromFile(file))
            }

            val degree = basicAct.photoCompat!!.getDegreesFromPath(currentPhotoPath!!)
            val rotatedBitmap = basicAct.photoCompat!!.rotateBitmap(bitmap, degree)
            val resizedBitmap = basicAct.photoCompat!!.resizeBitmapByDeviceRatio(rotatedBitmap!!,
                ratio!!, isWidthRatio)
            if(bitmap != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) + basicAct.photoCompat!!.getBase64FromBitmap(bitmap)
                val returnObj = Utils.returnJson(true, base64, null)
                cameraDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            } else {
                val returnObj = Utils.returnJson(true, null, "사진이 존재하지 않습니다")
                cameraDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
        } else {
            val returnObj = Utils.returnJson(true, null, "취소되었습니다")
            cameraDeviceAction?.promiseReturn(returnObj)
            ratio = null
            isWidthRatio = null
        }
    }

    val actionByRatio
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            cameraAction = action
            ratio = array[0].reified()
            val permissions = arrayOf(Constants.PERM_CAMERA, Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

            if(Utils.existAllPermission(permissions)) {
                dispatchTakePictureIntent()
            } else {
                permissionResult.launch(permissions)
            }
        }
    }

    /** onRequestPermissionResult */
    private val permissionResult = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if(Utils.existAllPermission(permissions)) {
            dispatchTakePictureIntent()
        } else {
            val deniedObj = Utils.returnJson(false, null, basicAct.getString(R.string.msg_denied_perm))
            cameraAction?.promiseReturn(deniedObj)
            ratio = null
        }
    }

    /** onActivityResult */
    private val activityResult = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
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

            if (bitmap != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) + basicAct.photoCompat!!.getBase64FromBitmap(bitmap)
                val returnObj = Utils.returnJson(true, base64, null)
                cameraAction?.promiseReturn(returnObj)
                ratio = null
            } else {
                val returnObj = Utils.returnJson(true, null, "사진이 존재하지 않습니다")
                cameraAction?.promiseReturn(returnObj)
                ratio = null
            }
        } else {
            val returnObj = Utils.returnJson(true, null, "취소되었습니다")
            cameraAction?.promiseReturn(returnObj)
            ratio = null
        }
        cameraAction?.resolveVoid()
    }
}
package com.example.hybridapp.util.module

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import java.io.ByteArrayOutputStream

object Photo {

    private val basicActivity = App.activity as BasicActivity

    /** 갤러리 호출 (1장) */
    fun requestImage(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?) {
        Constants.LOGE("requestImage", Constants.TAG_PHOTO)

        val singlePhotoIntent = getSinglePhotoIntent()
        singlePhotoIntent.putExtra("ratio", ratio)

        if(Utils.existsReceiveActivity(singlePhotoIntent, App.INSTANCE.packageManager)) {
            basicActivity.singlePhotoAction = action

            if(isWidthRatio != null) {
                singlePhotoIntent.putExtra("isWidthRatio", isWidthRatio)
                if(isWidthRatio) {
                    basicActivity.startActivityForResult(singlePhotoIntent, Constants.REQ_CODE_PHOTO_DEVICE_RATIO)
                } else {
                    basicActivity.startActivityForResult(singlePhotoIntent, Constants.REQ_CODE_PHOTO_DEVICE_RATIO)
                }
            } else {
                basicActivity.startActivityForResult(singlePhotoIntent, Constants.REQ_CODE_PHOTO_RATIO)
            }
        } else {
            Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_GALLERY)
            action?.promiseReturn(null)
        }
    }

    /** 갤러리 호출 (여러 장) */
    fun requestMultipleImages(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?) {
        Constants.LOGE("requestMultipleImages", Constants.TAG_PHOTO)

        val multiplePhotosIntent = getMultiplePhotosIntent()
        multiplePhotosIntent.putExtra("ratio", ratio)
        multiplePhotosIntent.putExtra("isWidthRatio", isWidthRatio)
        if(Utils.existsReceiveActivity(multiplePhotosIntent, App.INSTANCE.packageManager)) {
            basicActivity.multiplePhotosAction = action

            if(isWidthRatio != null) {
                multiplePhotosIntent.putExtra("isWidthRatio", isWidthRatio)
                if(isWidthRatio) {
                    basicActivity.startActivityForResult(multiplePhotosIntent, Constants.REQ_CODE_MULTI_PHOTO_DEVICE_RATIO)
                } else {
                    basicActivity.startActivityForResult(multiplePhotosIntent, Constants.REQ_CODE_MULTI_PHOTO_DEVICE_RATIO)
                }
            } else {
                basicActivity.startActivityForResult(multiplePhotosIntent, Constants.REQ_CODE_MULTI_PHOTO_RATIO)
            }
        } else {
            Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_GALLERY)
            action?.promiseReturn(null)
        }
    }

    /** 갤러리 인텐트 반환 */
    private fun getSinglePhotoIntent(): Intent {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE

        return galleryIntent
    }

    /** 갤러리 인텐트 반환 (여러 장) */
    private fun getMultiplePhotosIntent(): Intent {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE
        //galleryIntent.type = "image/*"
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        return galleryIntent
    }

    /** Uri->Base64로 변환 */
    fun convertUriToBase64(uri: Uri): String {
        Constants.LOGE("convertUriToBase64", Constants.TAG_PHOTO)

        val bitmap = convertUriToBitmap(uri)

        return convertBitmapToBase64(bitmap)
    }

    /** Uri->Bitmap으로 변환 */
    fun convertUriToBitmap(uri: Uri): Bitmap {
        Constants.LOGE("convertUriToBitmap", Constants.TAG_PHOTO)

        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val source
                    = ImageDecoder.createSource(App.INSTANCE.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(App.INSTANCE.contentResolver, uri)
        }
    }

    /** Bitmap->Base64 로 변환 */
    fun convertBitmapToBase64(bitmap: Bitmap): String {
        Constants.LOGE("convertBitmapToBase64", Constants.TAG_PHOTO)

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /** 디바이스 화면 비율에 맞게 리사이즈 */
    fun resizeBitmapByDeviceRatio(bitmap: Bitmap, ratio: Double, isWidthRatio: Boolean?): Bitmap {
        val displayMetrics = DisplayMetrics()
        App.activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        return if(isWidthRatio!!) {
            val resizeWidth = (screenWidth * ratio).toInt()
            val resizeHeight = (bitmap.height * ((screenWidth * ratio) / bitmap.width)).toInt()

            Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true)
        } else {
            val resizeWidth = (bitmap.height * ((screenHeight * ratio) / bitmap.height)).toInt()
            val resizeHeight = (screenHeight * ratio).toInt()

            Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true)
        }
    }

    /** 이미지 비율에 맞게 리사이즈 */
    fun resizeBitmapByRatio(bitmap: Bitmap, aspectRatio: Double): Bitmap {
        val width = (bitmap.width * aspectRatio).toInt()
        val height = (bitmap.height * aspectRatio).toInt()

        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }


}
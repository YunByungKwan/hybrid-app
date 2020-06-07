package com.example.hybridapp.util.module

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import java.io.ByteArrayOutputStream

object Photo {

    /** 갤러리 호출 (1장) */
    fun requestImage(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?) {
        Constants.LOGE("requestImage", Constants.TAG_PHOTO)

        val storagePermissions = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
            Constants.PERM_READ_EXTERNAL_STORAGE)

        if(Utils.existAllPermission(storagePermissions) && action != null) {
            Log.e(Constants.TAG_PHOTO, "Read/write storage permission exist.")

            val singlePhotoIntent = getSinglePhotoIntent()
            val packageManager = App.INSTANCE.packageManager
            val basicActivity = App.activity as BasicActivity
            basicActivity.ratio = ratio

            if(Utils.existsReceiveActivity(singlePhotoIntent, packageManager)) {
                if(isWidthRatio != null) {
                    basicActivity.photoDeviceAction = action
                    basicActivity.isWidthRatio = isWidthRatio

                    basicActivity.startActivityForResult(singlePhotoIntent,
                        Constants.REQ_CODE_PHOTO_DEVICE_RATIO)
                } else {
                    basicActivity.photoAction = action

                    basicActivity.startActivityForResult(singlePhotoIntent,
                        Constants.REQ_CODE_PHOTO_RATIO)
                }
            } else {
                Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_GALLERY)
                action.promiseReturn(null)
            }
        } else {
            Utils.checkDangerousPermissions(storagePermissions, Constants.REQ_PERM_CODE_READ_WRITE)
            action?.promiseReturn(null)
        }
    }

    /** 갤러리 호출 (여러 장) */
    fun requestMultipleImages(action: FlexAction?, ratio: Double, isWidthRatio: Boolean?) {
        Constants.LOGE("requestMultipleImages", Constants.TAG_PHOTO)

        val storagePermissions =
            arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

        if(Utils.existAllPermission(storagePermissions) && action != null) {
            Log.e("Action", "권한 다 있음")
            val multiplePhotosIntent = getMultiplePhotosIntent()
            val packageManager = App.INSTANCE.packageManager
            val basicActivity = App.activity as BasicActivity
            basicActivity.ratio = ratio

            if(Utils.existsReceiveActivity(multiplePhotosIntent, packageManager)) {
                Log.e("Photo object", "갤러리 앱 있음")
                if(isWidthRatio != null) {
                    Log.e("Photo object", "디바이스 기준으로 resize")
                    basicActivity.multiplePhotoDeviceAction = action
                    basicActivity.isWidthRatio = isWidthRatio
                    basicActivity.startActivityForResult(multiplePhotosIntent, Constants.REQ_CODE_MULTI_PHOTO_DEVICE_RATIO)
                } else {
                    basicActivity.multiplePhotosAction = action
                    basicActivity.startActivityForResult(multiplePhotosIntent, Constants.REQ_CODE_MULTI_PHOTO_RATIO)
                }
            } else {
                Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_GALLERY)
                action.promiseReturn(null)
            }
        } else {
            Utils.checkDangerousPermissions(storagePermissions, Constants.REQ_PERM_CODE_READ_WRITE)
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

    /** 비트맵 리사이징 후 base64로 변환 */
    fun convertUriToResizingBase64(imageUri: Uri?, ratio: Double?, isWidthRatio: Boolean?): String {
        val bitmap = convertUriToBitmap(imageUri!!)

        // isWidthRatio가 널일 경우 이미지 비율에 맞게 리사이즈
        val resizedBitmap = if(isWidthRatio == null) {
            resizeBitmapByRatio(bitmap, ratio!!)
        } else {
            // isWidthRatio가 널이 아닐 경우 디바이스에 맞게 리사이즈
            resizeBitmapByDeviceRatio(bitmap, ratio!!, isWidthRatio)
        }

        return convertBitmapToBase64(resizedBitmap)
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
        val screenWidth = Utils.getScreenSize(App.activity).getValue(Constants.SCREEN_WIDTH)
        val screenHeight = Utils.getScreenSize(App.activity).getValue(Constants.SCREEN_HEIGHT)

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
    fun resizeBitmapByRatio(bitmap: Bitmap, ratio: Double): Bitmap {
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}
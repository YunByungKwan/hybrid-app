package com.example.hybridapp.util.module

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

object Photo {

    /** 갤러리 호출 (1장) */
    fun requestImage(action: FlexAction?, ratio: Double?, isWidthRatio: Boolean?) {
        Constants.LOGD("Call requestImage()")

        val storagePerms = arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
            Constants.PERM_READ_EXTERNAL_STORAGE)

        if(Utils.existAllPermission(storagePerms) && action != null) {
            Constants.LOGD("Read/write storage permission exist.")

            val singlePhotoIntent = getSinglePhotoIntent()
            val packageManager = App.INSTANCE.packageManager
            val basicActivity = App.activity as BasicActivity
            basicActivity.ratio = ratio

            if(Utils.existsReceiveActivity(singlePhotoIntent, packageManager)) {
                if(isWidthRatio != null) {
                    basicActivity.photoDeviceAction = action
                    basicActivity.isWidthRatio = isWidthRatio

                    basicActivity.startActivityForResult(singlePhotoIntent,
                        Constants.PHOTO_DEVICE_RATIO_REQ_CODE)
                } else {
                    basicActivity.photoAction = action

                    basicActivity.startActivityForResult(singlePhotoIntent,
                        Constants.PHOTO_RATIO_REQ_CODE)
                }
            } else {
                Constants.LOGE(Constants.LOG_MSG_GALLERY)
                action.resolveVoid()
            }
        } else {
            Utils.checkDangerousPermissions(storagePerms, Constants.PERM_READ_WRITE_REQ_CODE)
            action?.resolveVoid()
        }
    }

    /** 갤러리 호출 (여러 장) */
    fun requestMultipleImages(action: FlexAction?, ratio: Double, isWidthRatio: Boolean?) {
        Constants.LOGE("Call requestMultipleImages()")

        val storagePerms =
            arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                Constants.PERM_READ_EXTERNAL_STORAGE)

        if(Utils.existAllPermission(storagePerms) && action != null) {
            val multiplePhotosIntent = getMultiplePhotosIntent()
            val packageManager = App.INSTANCE.packageManager
            val basicActivity = App.activity as BasicActivity
            basicActivity.ratio = ratio

            if(Utils.existsReceiveActivity(multiplePhotosIntent, packageManager)) {
                if(isWidthRatio != null) {
                    basicActivity.multiplePhotoDeviceAction = action
                    basicActivity.isWidthRatio = isWidthRatio
                    basicActivity.startActivityForResult(multiplePhotosIntent,
                        Constants.MULTI_PHOTO_DEVICE_RATIO_REQ_CODE)
                } else {
                    basicActivity.multiplePhotosAction = action
                    basicActivity.startActivityForResult(multiplePhotosIntent,
                        Constants.MULTI_PHOTO_RATIO_REQ_CODE)
                }
            } else {
                Constants.LOGE(Constants.LOG_MSG_GALLERY)
                action.resolveVoid()
            }
        } else {
            Utils.checkDangerousPermissions(storagePerms, Constants.PERM_READ_WRITE_REQ_CODE)
            action?.resolveVoid()
        }
    }

    /** 갤러리 인텐트 반환 */
    private fun getSinglePhotoIntent(): Intent {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE
//        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            MediaStore.Images.Media.CONTENT_TYPE)
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
        val bitmap = getBitmapFromUri(imageUri!!)

        // isWidthRatio가 널일 경우 이미지 비율에 맞게 리사이즈
        val resizedBitmap = if(isWidthRatio == null) {
            resizeBitmapByRatio(bitmap!!, ratio!!)
        } else {
            // isWidthRatio가 널이 아닐 경우 디바이스에 맞게 리사이즈
            resizeBitmapByDeviceRatio(bitmap!!, ratio!!, isWidthRatio)
        }

        return getBase64FromBitmap(resizedBitmap)
    }

    /** Uri->Base64로 변환 */
    fun getBase64FromUri(uri: Uri): String {
        Constants.LOGD("Call getBase64FromUri()")

        val bitmap = getBitmapFromUri(uri)

        return getBase64FromBitmap(bitmap!!)
    }

    /** Uri --> Bitmap */
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        Constants.LOGD("Call getBitmapFromUri()")

        val filePath = getFilePathFromUri(uri)
        val degrees = getDegreesFromPath(filePath)
        val bitmap = createBitmapFromFilePath(filePath)

        return rotateBitmap(bitmap, degrees)
    }

    /** Uri --> File Path */
    private fun getFilePathFromUri(uri: Uri): String {
        Constants.LOGD("Call getFilePathFromUri()")

        var cursor: Cursor? = null

        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            cursor = (App.activity).contentResolver.query(
                uri, projection, null, null, null
            )
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()

            Constants.LOGD("Uri --> File path: ${uri.path} --> ${cursor.getString(columnIndex)!!}")

            return cursor.getString(columnIndex)!!
        } finally {
            cursor?.close()
        }

        return ""
    }

    /** FilePath의 회전 각도를 반환 */
    private fun getDegreesFromPath(filePath: String): Int {
        Constants.LOGD("Call getDegreesFromPath()")

        val exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)

        return getDegreesFromExifOrientation(orientation)
    }

    /** exifInterface orientation --> degrees */
    private fun getDegreesFromExifOrientation(orientation: Int): Int {
        Constants.LOGD("Call getDegreesFromExifOrientation()")

        return when(orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                Constants.LOGD("$orientation --> 90")
                90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                Constants.LOGD("$orientation --> 180")
                180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                Constants.LOGD("$orientation --> 270")
                270
            }
            else -> {
                Constants.LOGD("$orientation --> 0")
                0
            }
        }
    }

    /** 파일 경로로부터 비트맵 생성 */
    private fun createBitmapFromFilePath(filePath: String): Bitmap? {
        Constants.LOGD("Call createBitmapFromFilePath()")

        var bitmap: Bitmap? = null

        try {
            val file = File(filePath)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            bitmap = BitmapFactory.decodeStream(FileInputStream(file), null, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    /** 비트맵 회전 */
    private fun rotateBitmap(bitmap: Bitmap?, degree: Int): Bitmap? {
        Constants.LOGD("Call rotateBitmap()")

        if(bitmap == null) {
            Constants.LOGD("Bitmap is null")

            return null
        }

        val matrix = Matrix()
        val degrees = degree.toFloat()
        val px = bitmap.width.toFloat() / 2
        val py = bitmap.height.toFloat() / 2
        matrix.setRotate(degrees, px, py)

        return Bitmap.createBitmap(bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true)
    }

    /** Bitmap --> Base64 */
    fun getBase64FromBitmap(bitmap: Bitmap): String {
        Constants.LOGD("Call getBase64FromBitmap()")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /** 디바이스 화면 비율에 맞게 리사이즈 */
    private fun resizeBitmapByDeviceRatio(bitmap: Bitmap, ratio: Double, isWidthRatio: Boolean?): Bitmap {
        val screenWidth = Utils.getScreenSize().getValue(Constants.SCREEN_WIDTH)
        val screenHeight = Utils.getScreenSize().getValue(Constants.SCREEN_HEIGHT)

        Log.d("dlgodnjs", ratio.toString())
        return if(isWidthRatio!!) {
            Constants.LOGD("Resize bitmap by device width ratio(${ratio*100}%)")

            val resizeWidth = (screenWidth * ratio).toInt()
            val resizeHeight = (bitmap.height * ((screenWidth * ratio) / bitmap.width)).toInt()

            Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true)
        } else {
            Constants.LOGD("Resize bitmap by device height ratio(${ratio*100}%)")

            val resizeWidth = (bitmap.height * ((screenHeight * ratio) / bitmap.height)).toInt()
            val resizeHeight = (screenHeight * ratio).toInt()

            Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true)
        }
    }

    /** 이미지 비율에 맞게 리사이즈 */
    private fun resizeBitmapByRatio(bitmap: Bitmap, ratio: Double): Bitmap {
        Constants.LOGD("Call resizeBitmapByRatio()")

        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        Constants.LOGD("Resize width: $width, height: $height")

        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}
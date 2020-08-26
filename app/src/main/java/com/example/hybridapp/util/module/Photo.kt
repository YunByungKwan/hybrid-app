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
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

object Photo {

    /** 갤러리 호출 (1장) */
    fun requestImage() {
        Utils.LOGD("Call requestImage()")

        val basicActivity = App.activity as BasicActivity
        val packageManager = App.INSTANCE.packageManager
        val singlePhotoIntent = getSinglePhotoIntent()

        // 갤러리 앱을 사용할 수 있는 경우
        if(Utils.existsReceiveActivity(singlePhotoIntent, packageManager)) {
            // 디바이스 기준으로 resize
            if(basicActivity.isWidthRatio != null) {
                basicActivity.startActivityForResult(singlePhotoIntent,
                    Constants.PHOTO_DEVICE_RATIO_REQ_CODE)
            }
            // 이미지 기준으로 resize
            else {
                basicActivity.startActivityForResult(singlePhotoIntent,
                    Constants.PHOTO_RATIO_REQ_CODE)
            }
        }
        // 갤러리 앱을 사용할 수 없는 경우
        else {
            val returnObj = Utils.createJSONObject(true,
                null, App.context().getString(R.string.msg_not_load_gallery))

            // 디바이스 기준일 경우
            if(basicActivity.isWidthRatio != null) {
                basicActivity.photoDeviceAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
                basicActivity.isWidthRatio = null
            }
            // 이미지 기준일 경우우
           else {
                basicActivity.photoAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
            }
        }
    }

    /** 갤러리 호출 (여러 장) */
    fun requestMultipleImages() {
        Utils.LOGD("Call requestMultipleImages()")

        val packageManager = App.INSTANCE.packageManager
        val basicActivity = App.activity as BasicActivity
        val multiplePhotosIntent = getMultiplePhotosIntent()

        // 갤러리 앱을 사용할 수 있는 경우
        if(Utils.existsReceiveActivity(multiplePhotosIntent, packageManager)) {
            // 디바이스 기준으로 resize
            if(basicActivity.isWidthRatio != null) {
                basicActivity.startActivityForResult(multiplePhotosIntent,
                    Constants.MULTI_PHOTO_DEVICE_RATIO_REQ_CODE)
            }
            // 이미지 기준으로 resize
            else {
                basicActivity.startActivityForResult(multiplePhotosIntent,
                    Constants.MULTI_PHOTO_RATIO_REQ_CODE)
            }
        }
        // 갤러리 앱을 사용할 수 있는 경우
        else {
            val returnObj = Utils.createJSONObject(true,
                null, App.INSTANCE.getString(R.string.msg_not_load_gallery))

            // 디바이스 기준일 경우
            if(basicActivity.isWidthRatio != null) {
                basicActivity.multiplePhotoDeviceAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
                basicActivity.isWidthRatio = null
            }
            // 이미지 기준일 경우
            else {
                basicActivity.multiplePhotoAction?.promiseReturn(returnObj)
                basicActivity.ratio = null
            }
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
        Utils.LOGD("Call convertUriToResizingBase64()")

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

    fun temp64 (imageUri: Uri?, ratio: Double?, isWidthRatio: Boolean?): Bitmap {
        val bitmap = getBitmapFromUri(imageUri!!)

        // isWidthRatio가 널일 경우 이미지 비율에 맞게 리사이즈
        val resizedBitmap = if(isWidthRatio == null) {
            Log.d("dlgodnjs", "resized")
            resizeBitmapByRatio(bitmap!!, ratio!!)
        } else {
            // isWidthRatio가 널이 아닐 경우 디바이스에 맞게 리사이즈
            Log.d("dlgodnjs", "isWidthRatio : $isWidthRatio")
            resizeBitmapByDeviceRatio(bitmap!!, ratio!!, isWidthRatio)
        }

        return resizedBitmap
    }

    /** Uri->Base64로 변환 */
    fun getBase64FromUri(uri: Uri): String {
        Utils.LOGD("Call getBase64FromUri()")

        val bitmap = getBitmapFromUri(uri)

        return getBase64FromBitmap(bitmap!!)
    }

    /** Uri --> Bitmap */
    fun getBitmapFromUri(uri: Uri): Bitmap? {
        Utils.LOGD("Call getBitmapFromUri()")

        val filePath = getFilePathFromUri(uri)
        val degrees = getDegreesFromPath(filePath)
        val bitmap = createBitmapFromFilePath(filePath)

        return rotateBitmap(bitmap, degrees)
    }

    /** Uri --> File Path */
    fun getFilePathFromUri(uri: Uri): String {
        Utils.LOGD("Call getFilePathFromUri()")

        var cursor: Cursor? = null

        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            cursor = (App.activity).contentResolver.query(
                uri, projection, null, null, null
            )
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()

            Utils.LOGD("Uri --> File path: ${uri.path} --> ${cursor.getString(columnIndex)!!}")

            return cursor.getString(columnIndex)!!
        } finally {
            cursor?.close()
        }
    }

    /** FilePath의 회전 각도를 반환 */
    fun getDegreesFromPath(filePath: String): Int {
        Utils.LOGD("Call getDegreesFromPath()")

        val exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)

        return getDegreesFromExifOrientation(orientation)
    }

    /** exifInterface orientation --> degrees */
    private fun getDegreesFromExifOrientation(orientation: Int): Int {
        Utils.LOGD("Call getDegreesFromExifOrientation()")

        return when(orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                Utils.LOGD("$orientation --> 90")
                90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                Utils.LOGD("$orientation --> 180")
                180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                Utils.LOGD("$orientation --> 270")
                270
            }
            else -> {
                Utils.LOGD("$orientation --> 0")
                0
            }
        }
    }

    /** 파일 경로로부터 비트맵 생성 */
    private fun createBitmapFromFilePath(filePath: String): Bitmap? {
        Utils.LOGD("Call createBitmapFromFilePath()")

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
    fun rotateBitmap(bitmap: Bitmap?, degree: Int): Bitmap? {
        Utils.LOGD("Call rotateBitmap()")

        if(bitmap == null) {
            Utils.LOGE("Bitmap is null")

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
        Utils.LOGD("Call getBase64FromBitmap() " +
                "Bitmap width: ${bitmap.width}, height: ${bitmap.height}")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /** 디바이스 화면 비율에 맞게 리사이즈 */
    fun resizeBitmapByDeviceRatio(bitmap: Bitmap, ratio: Double, isWidthRatio: Boolean?): Bitmap {
        Utils.LOGD("Call resizeBitmapByDeviceRatio()")
        val screenWidth = Utils.getScreenSize().getValue(App.INSTANCE.getString(R.string.screen_width))
        val screenHeight = Utils.getScreenSize().getValue(App.INSTANCE.getString(R.string.screen_height))

        Utils.LOGD("screenWidth: $screenWidth screenHeight: $screenHeight")
        Utils.LOGD("bitmap Width: ${bitmap.width} bitmap Height: ${bitmap.height}")

        Utils.LOGD("ratio: $ratio")
        return if(isWidthRatio!!) {
            Utils.LOGD("Resize bitmap by device width ratio(${ratio*100}%)")

            val resizeWidth = (screenWidth * ratio).toInt()
            val resizeHeight = (bitmap.height * ((screenWidth * ratio) / bitmap.width)).toInt()

            Utils.LOGD("resizeWidth : $resizeWidth resizeHeight : $resizeHeight")

            Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true)
        } else {
            Utils.LOGD("Resize bitmap by device height ratio(${ratio*100}%)")

            val resizeWidth = (bitmap.width * ((screenHeight * ratio) / bitmap.height)).toInt()
            val resizeHeight = (screenHeight * ratio).toInt()

            Utils.LOGD("resizeWidth : $resizeWidth resizeHeight : $resizeHeight")

            Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true)
        }
    }

    /** 이미지 비율에 맞게 리사이즈 */
    fun resizeBitmapByRatio(bitmap: Bitmap, ratio: Double): Bitmap {
        Utils.LOGD("Call resizeBitmapByRatio()")

        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        Utils.LOGD("Resize width: $width, height: $height")

        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

}
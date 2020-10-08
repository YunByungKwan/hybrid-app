package com.example.hybridapp.module

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.result.registerForActivityResult
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

class Photo(private val basicActivity: BasicActivity) {

    var photoDeviceAction: FlexAction? = null
    var photoAction: FlexAction? = null
    var multiplePhotoDeviceAction: FlexAction? = null
    var multiplePhotoAction: FlexAction? = null

    var ratio: Double? = null
    var isWidthRatio: Boolean? = null

    /**======================================== Action ===========================================*/
    val actionByDeviceRatioSingle
            = FlexLambda.action{ action, array ->
        withContext(Dispatchers.Main) {
            photoDeviceAction = action
            ratio = array[0].reified()
            isWidthRatio = array[1].reified()

            if(Utils.existAllPermission(arrayOf(
                    Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photoInstance!!.takeSingleImage()
            } else {
                basicActivity.photoInstance!!.requestPermissionResultByDeviceSingle.launch()
            }
        }
    }

    val actionByRatioSingle
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            photoAction = action
            ratio = array[0].reified()

            if(Utils.existAllPermission(arrayOf(
                    Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photoInstance!!.takeSingleImage()
            } else {
                basicActivity.photoInstance!!.requestPermissionResultSingle.launch()
            }
        }
    }

    val actionByDeviceRatioMulti
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            multiplePhotoDeviceAction = action
            ratio = array[0].reified()
            isWidthRatio = array[1].reified()

            if(Utils.existAllPermission(arrayOf(
                    Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photoInstance!!.takeMultipleImages()
            } else {
                basicActivity.photoInstance!!.requestPermissionResultByDeviceMulti.launch()
            }
        }
    }

    val actionByRatioMulti
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            multiplePhotoAction = action
            ratio = array[0].reified()

            if(Utils.existAllPermission(arrayOf(
                    Constants.PERM_WRITE_EXTERNAL_STORAGE,
                    Constants.PERM_READ_EXTERNAL_STORAGE))) {
                basicActivity.photoInstance!!.takeMultipleImages()
            } else {
                basicActivity.photoInstance!!.requestPermissionResultMulti.launch()
            }
        }
    }

    /** onRequestPermissionResult */
    val requestPermissionResultByDeviceSingle = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), READ_EXTERNAL_STORAGE) { isGranted ->
        if(isGranted) {
            takeSingleImage()
        } else {
            val deniedObj = Utils.createJSONObject(false, null,
                basicActivity.getString(R.string.msg_denied_perm))
            photoDeviceAction?.promiseReturn(deniedObj)
            ratio = null
            isWidthRatio = null
        }
    }

    val requestPermissionResultSingle = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), READ_EXTERNAL_STORAGE) { isGranted ->
        if(isGranted) {
            takeSingleImage()
        } else {
            val deniedObj = Utils.createJSONObject(false, null,
                basicActivity.getString(R.string.msg_denied_perm))
            photoAction?.promiseReturn(deniedObj)
            ratio = null
        }
    }

    val requestPermissionResultByDeviceMulti = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), READ_EXTERNAL_STORAGE) { isGranted ->
        if(isGranted) {
            takeSingleImage()
        } else {
            val deniedObj = Utils.createJSONObject(false, null,
                basicActivity.getString(R.string.msg_denied_perm))
            multiplePhotoDeviceAction?.promiseReturn(deniedObj)
            ratio = null
            isWidthRatio = null
        }
    }

    val requestPermissionResultMulti = basicActivity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(), READ_EXTERNAL_STORAGE) { isGranted ->
        if(isGranted) {
            takeSingleImage()
        } else {
            val deniedObj = Utils.createJSONObject(false, null,
                basicActivity.getString(R.string.msg_denied_perm))
            multiplePhotoAction?.promiseReturn(deniedObj)
            ratio = null
        }
    }

    /** onActivityResult */
    private val photoDeviceRatioForResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val data = result.data

        // 사진 불러오기 성공
        if(result.resultCode == Activity.RESULT_OK) {
            if(data != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) +
                        convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                val returnObj = Utils.createJSONObject(true,
                    base64, null)
                photoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            } else {
                Utils.LOGE("사진이 존재하지 않습니다")
                val returnObj = Utils.createJSONObject(true,
                    null, "사진이 존재하지 않습니다")
                photoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
        }
        // 사진 불러오기 실패
        else {
            Utils.LOGE("취소되었습니다")
            val returnObj = Utils.createJSONObject(true,
                null, "취소되었습니다")
            photoDeviceAction?.promiseReturn(returnObj)
            ratio = null
            isWidthRatio = null
        }
    }

    private val photoRatioForResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d("Test", "Call photoRatioForResult")
        val data = result.data

        // 사진 불러오기 성공
        if(result.resultCode == Activity.RESULT_OK) {
            ratio = if(data != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) +
                        basicActivity.photoInstance!!.convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                val returnObj = Utils.createJSONObject(true,
                    base64, null)
                photoAction?.promiseReturn(returnObj)
                null
            } else {
                Utils.LOGE("사진이 존재하지 않습니다")
                val returnObj = Utils.createJSONObject(true,
                    null, "사진이 존재하지 않습니다")
                photoAction?.promiseReturn(returnObj)
                null
            }
        }
        // 사진 불러오기 실패
        else {
            Utils.LOGE("취소되었습니다")
            val returnObj = Utils.createJSONObject(true,
                null, "취소되었습니다")
            photoAction?.promiseReturn(returnObj)
            ratio = null
        }
    }

    private val multiPhotoDeviceRatioForResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val data = result.data

        // 사진 불러오기 성공
        if(result.resultCode == Activity.RESULT_OK) {
            val base64Images = ArrayList<String>()
            if(data != null) {
                val clipData = data.clipData
                if(clipData?.itemCount in 0..9) {
                    for(i in 0 until clipData?.itemCount!!) {
                        val imageUri = clipData.getItemAt(i).uri
                        val base64 = App.INSTANCE.getString(R.string.base64_url) +
                                basicActivity.photoInstance!!.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                        base64Images.add(base64)
                        Utils.LOGD("${i + 1}번째 : $base64")
                    }

                    val returnObj = JSONObject()
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_auth), true)
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), base64Images.toTypedArray())
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), null)
                    multiplePhotoDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
                } else {
                    Toast.showLongToast("10장 이상의 사진을 첨부할 수 없습니다")
                    val returnObj = Utils.createJSONObject(true,
                        null, "10장 이상의 사진을 첨부할 수 없습니다")
                    multiplePhotoDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
                }
            } else {
                Utils.LOGE("Data is null")
                val returnObj = Utils.createJSONObject(true,
                    null, "사진을 첨부할 수 없습니다")
                multiplePhotoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
        }
        // 사진 불러오기 실패
        else {
            Utils.LOGE("MULTI PHOTO BY DEVICE RATIO RESULT CANCELED")
            val returnObj = Utils.createJSONObject(true,
                null, "취소되었습니다")
            multiplePhotoDeviceAction?.promiseReturn(returnObj)
            ratio = null
            isWidthRatio = null
        }
    }

    private val multiPhotoRatioForResult = basicActivity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d("Test", "Call multiPhotoRatioForResult")
        val data = result.data

        if(result.resultCode == Activity.RESULT_OK) {
            val base64Images = ArrayList<String>()
            if(data != null) {
                val clipData = data.clipData
                if(clipData?.itemCount in 1..9) {
                    for(idx in 0 until clipData?.itemCount!!) {
                        val imageUri = clipData.getItemAt(idx).uri
                        val base64 = App.INSTANCE.getString(R.string.base64_url) +
                                basicActivity.photoInstance!!.convertUriToResizingBase64(imageUri, ratio, isWidthRatio)

                        base64Images.add(base64)
                    }
                    multiplePhotoAction?.promiseReturn(base64Images)
                    ratio = null
                } else {
                    Toast.showLongToast("10장 이상의 사진을 첨부할 수 없습니다.")
                    val returnObj = Utils.createJSONObject(true,
                        null, "10장 이상의 사진을 첨부할 수 없습니다")
                    multiplePhotoAction?.promiseReturn(returnObj)
                    ratio = null
                }
            } else {
                Utils.LOGE("Data is null")
                val returnObj = Utils.createJSONObject(true,
                    null, "사진을 첨부할 수 없습니다")
                multiplePhotoAction?.promiseReturn(returnObj)
                ratio = null
            }
        } else {
            Utils.LOGE("MULTI PHOTO BY RATIO RESULT CANCELED")
            val returnObj = Utils.createJSONObject(true,
                null, "취소되었습니다")
            multiplePhotoAction?.promiseReturn(returnObj)
            ratio = null
        }
    }

    /** 갤러리 호출 (1장) */
    fun takeSingleImage() {
        val packageManager = App.INSTANCE.packageManager
        val singlePhotoIntent = getSinglePhotoIntent()
        if(Utils.existsReceiveActivity(singlePhotoIntent, packageManager)) {
            if(isWidthRatio != null) {
                photoDeviceRatioForResult.launch(singlePhotoIntent)
            } else {
                photoRatioForResult.launch(singlePhotoIntent)
            }
        }
        // 갤러리 앱을 사용할 수 없는 경우
        else {
            val returnObj = Utils.createJSONObject(true,
                null, App.context().getString(R.string.msg_not_load_gallery))

            // 디바이스 기준일 경우
            if(isWidthRatio != null) {
                photoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
            // 이미지 기준일 경우우
           else {
                photoAction?.promiseReturn(returnObj)
                ratio = null
            }
        }
    }

    /** 갤러리 호출 (여러 장) */
    fun takeMultipleImages() {
        val packageManager = App.INSTANCE.packageManager
        val multiplePhotosIntent = getMultiplePhotosIntent()

        if(Utils.existsReceiveActivity(multiplePhotosIntent, packageManager)) {
            if(isWidthRatio != null) {
                multiPhotoDeviceRatioForResult.launch(multiplePhotosIntent)
            } else {
                multiPhotoRatioForResult.launch(multiplePhotosIntent)
            }
        } else {
            val returnObj = Utils.createJSONObject(true,
                null, App.INSTANCE.getString(R.string.msg_not_load_gallery))

            // 디바이스 기준일 경우
            if(isWidthRatio != null) {
                multiplePhotoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
            // 이미지 기준일 경우
            else {
                multiplePhotoAction?.promiseReturn(returnObj)
                ratio = null
            }
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
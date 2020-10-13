package com.example.hybridapp.module

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

class PhotoCompat(private val basicAct: BasicActivity) {

    var photoDeviceAction: FlexAction? = null
    var photoAction: FlexAction? = null
    var multiplePhotoDeviceAction: FlexAction? = null
    var multiplePhotoAction: FlexAction? = null
    var ratio: Double? = null
    var isWidthRatio: Boolean? = null

    /** 갤러리 호출 (1장) */
    private fun dispatchSingleImageIntent() {
        val packageManager = App.INSTANCE.packageManager
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = MediaStore.Images.Media.CONTENT_TYPE
        }

        if(Utils.existsReceiveActivity(intent, packageManager)) {
            if(isResizedByDeviceRatio()) {
                photoDeviceRatioForResult.launch(intent)
            } else {
                photoRatioForResult.launch(intent)
            }
        } else {
            val returnObj = Utils.returnJson(true,
                null, basicAct.getString(R.string.msg_not_load_gallery))

            if(isResizedByDeviceRatio()) {
                photoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            } else {
                photoAction?.promiseReturn(returnObj)
                ratio = null
            }
        }
    }

    /** 갤러리 호출 (여러 장) */
    private fun dispatchMultipleImageIntent() {
        val packageManager = App.INSTANCE.packageManager
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = MediaStore.Images.Media.CONTENT_TYPE
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        if(Utils.existsReceiveActivity(intent, packageManager)) {
            if(isResizedByDeviceRatio()) {
                multiPhotoDeviceRatioForResult.launch(intent)
            } else {
                multiPhotoRatioForResult.launch(intent)
            }
        } else {
            val returnObj = Utils.returnJson(true,
                null, basicAct.getString(R.string.msg_not_load_gallery))

            // 디바이스 기준일 경우
            if(isResizedByDeviceRatio()) {
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

    /** 기기 비율에 따라 이미지를 리사이즈하는지 확인 */
    private fun isResizedByDeviceRatio(): Boolean = isWidthRatio != null

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
        val bitmap = getBitmapFromUri(uri)
        return getBase64FromBitmap(bitmap!!)
    }

    /** Uri --> Bitmap */
    fun getBitmapFromUri(uri: Uri): Bitmap? {
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
        val screenWidth = Utils.getScreenSize().getValue(App.INSTANCE.getString(R.string.screen_width))
        val screenHeight = Utils.getScreenSize().getValue(App.INSTANCE.getString(R.string.screen_height))
        return if(isWidthRatio!!) {
            val resizeWidth = (screenWidth * ratio).toInt()
            val resizeHeight = (bitmap.height * ((screenWidth * ratio) / bitmap.width)).toInt()
            Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true)
        } else {
            val resizeWidth = (bitmap.width * ((screenHeight * ratio) / bitmap.height)).toInt()
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

    /**======================================== Action ===========================================*/

    val actionByDeviceRatioSingle
            = FlexLambda.action{ action, array ->
        withContext(Dispatchers.Main) {
            val permission = Constants.PERM_READ_EXTERNAL_STORAGE
            photoDeviceAction = action
            ratio = array[0].reified()
            isWidthRatio = array[1].reified()

            if(Utils.existsPermission(permission)) {
                dispatchSingleImageIntent()
            } else {
                permissionResultByDeviceSingle.launch(permission)
            }
        }
    }

    val actionByRatioSingle
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            val permission = Constants.PERM_READ_EXTERNAL_STORAGE
            photoAction = action
            ratio = array[0].reified()

            if(Utils.existsPermission(permission)) {
                dispatchSingleImageIntent()
            } else {
                permissionResultSingle.launch(permission)
            }
        }
    }

    val actionByDeviceRatioMulti
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            val permission = Constants.PERM_READ_EXTERNAL_STORAGE
            multiplePhotoDeviceAction = action
            ratio = array[0].reified()
            isWidthRatio = array[1].reified()

            if(Utils.existsPermission(permission)) {
                dispatchMultipleImageIntent()
            } else {
                permissionResultByDeviceMulti.launch(permission)
            }
        }
    }

    val actionByRatioMulti
            = FlexLambda.action { action, array ->
        withContext(Dispatchers.Main) {
            val permission = Constants.PERM_READ_EXTERNAL_STORAGE
            multiplePhotoAction = action
            ratio = array[0].reified()

            if(Utils.existsPermission(permission)) {
                dispatchMultipleImageIntent()
            } else {
                permissionResultMulti.launch(permission)
            }
        }
    }

    /** onRequestPermissionResult */
    private val permissionResultByDeviceSingle = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            dispatchSingleImageIntent()
        } else {
            photoDeviceAction?.promiseReturn(Utils.deniedJson())
            ratio = null
            isWidthRatio = null
        }
    }

    private val permissionResultSingle = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            dispatchSingleImageIntent()
        } else {
            photoAction?.promiseReturn(Utils.deniedJson())
            ratio = null
        }
    }

    private val permissionResultByDeviceMulti = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            dispatchSingleImageIntent()
        } else {
            multiplePhotoDeviceAction?.promiseReturn(Utils.deniedJson())
            ratio = null
            isWidthRatio = null
        }
    }

    private val permissionResultMulti = basicAct.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted) {
            dispatchSingleImageIntent()
        } else {
            multiplePhotoAction?.promiseReturn(Utils.deniedJson())
            ratio = null
        }
    }

    /**================================== onActivityResult =======================================*/

    private val photoDeviceRatioForResult = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val data = result.data

        if(result.resultCode == Activity.RESULT_OK) {
            if(data != null) {
                val base64 = basicAct.getString(R.string.base64_url) +
                        convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                val returnObj = Utils.returnJson(true, base64, null)
                photoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            } else {
                val returnObj = Utils.returnJson(true, null, "사진이 존재하지 않습니다")
                photoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
        } else {
            val returnObj = Utils.returnJson(true, null, "취소되었습니다")
            photoDeviceAction?.promiseReturn(returnObj)
            ratio = null
            isWidthRatio = null
        }
    }

    private val photoRatioForResult = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val data = result.data

        if(result.resultCode == Activity.RESULT_OK) {
            ratio = if(data != null) {
                val base64 = App.INSTANCE.getString(R.string.base64_url) +
                        convertUriToResizingBase64(data.data, ratio, isWidthRatio)

                val returnObj = Utils.returnJson(true, base64, null)
                photoAction?.promiseReturn(returnObj)
                null
            } else {
                val returnObj = Utils.returnJson(true, null, "사진이 존재하지 않습니다")
                photoAction?.promiseReturn(returnObj)
                null
            }
        } else {
            val returnObj = Utils.returnJson(true,
                null, "취소되었습니다")
            photoAction?.promiseReturn(returnObj)
            ratio = null
        }
    }

    private val multiPhotoDeviceRatioForResult = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val data = result.data

        if(result.resultCode == Activity.RESULT_OK) {
            val base64Images = ArrayList<String>()
            if(data != null) {
                val clipData = data.clipData
                if(clipData?.itemCount in 0..9) {
                    for(i in 0 until clipData?.itemCount!!) {
                        val imageUri = clipData.getItemAt(i).uri
                        val base64 = App.INSTANCE.getString(R.string.base64_url) +
                                convertUriToResizingBase64(imageUri, ratio, isWidthRatio)
                        base64Images.add(base64)
                    }

                    val returnObj = JSONObject()
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_auth), true)
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_data), base64Images.toTypedArray())
                    returnObj.put(App.INSTANCE.getString(R.string.obj_key_msg), null)
                    multiplePhotoDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
                } else {
                    val returnObj = Utils.returnJson(true,
                        null, "10장 이상의 사진을 첨부할 수 없습니다")
                    multiplePhotoDeviceAction?.promiseReturn(returnObj)
                    ratio = null
                    isWidthRatio = null
                }
            } else {
                val returnObj = Utils.returnJson(true,
                    null, "사진을 첨부할 수 없습니다")
                multiplePhotoDeviceAction?.promiseReturn(returnObj)
                ratio = null
                isWidthRatio = null
            }
        } else {
            val returnObj = Utils.returnJson(true, null, "취소되었습니다")
            multiplePhotoDeviceAction?.promiseReturn(returnObj)
            ratio = null
            isWidthRatio = null
        }
    }

    private val multiPhotoRatioForResult = basicAct.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val data = result.data

        if(result.resultCode == Activity.RESULT_OK) {
            val base64Images = ArrayList<String>()
            if(data != null) {
                val clipData = data.clipData
                if(clipData?.itemCount in 1..9) {
                    for(idx in 0 until clipData?.itemCount!!) {
                        val imageUri = clipData.getItemAt(idx).uri
                        val base64 = App.INSTANCE.getString(R.string.base64_url) +
                                convertUriToResizingBase64(imageUri, ratio, isWidthRatio)

                        base64Images.add(base64)
                    }
                    multiplePhotoAction?.promiseReturn(base64Images)
                    ratio = null
                } else {
                    val returnObj = Utils.returnJson(true,
                        null, "10장 이상의 사진을 첨부할 수 없습니다")
                    multiplePhotoAction?.promiseReturn(returnObj)
                    ratio = null
                }
            } else {
                val returnObj = Utils.returnJson(true,
                    null, "사진을 첨부할 수 없습니다")
                multiplePhotoAction?.promiseReturn(returnObj)
                ratio = null
            }
        } else {
            val returnObj = Utils.returnJson(true,
                null, "취소되었습니다")
            multiplePhotoAction?.promiseReturn(returnObj)
            ratio = null
        }
    }
}
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
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.ByteArrayOutputStream

object Photo {
    
    /** 갤러리 호출 (1장) */
    fun requestSingle() {
        Constants.LOGE("requestSingle", Constants.TAG_PHOTO)

        val singlePhotoIntent = getSinglePhotoIntent()

        if(Utils().existsReceiveActivity(singlePhotoIntent, App.INSTANCE.packageManager)) {
            App.activity.startActivityForResult(singlePhotoIntent, Constants.REQ_CODE_SINGLE_PHOTO)
        } else {
            Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_GALLERY)
        }
    }

    /** 갤러리 호출 (여러 장) */
    fun requestMultiple() {
        Constants.LOGE("requestMultiple", Constants.TAG_PHOTO)

        val multiplePhotosIntent = getMultiplePhotosIntent()

        if(Utils().existsReceiveActivity(multiplePhotosIntent, App.INSTANCE.packageManager)) {
            App.activity.startActivityForResult(multiplePhotosIntent, Constants.REQ_CODE_MULTIPLE_PHOTO)
        } else {
            Log.e(Constants.TAG_UTILS, Constants.LOG_MSG_GALLERY)
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
    private fun convertUriToBitmap(uri: Uri): Bitmap {
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

    val lambda: (FlexAction?, JSONArray?) -> Unit = { action, _ ->
        CoroutineScope(Dispatchers.Main).launch {
//            funLOGE(getString(R.string.type_camera))

            if(Utils().existAllPermission(arrayOf(Constants.PERM_CAMERA))) {
                if (action != null) {
                    Camera.request(action)
                }
            } else {
                Utils().requestDangerousPermissions(arrayOf(Constants.PERM_CAMERA),
                    Constants.REQ_PERM_CODE_CAMERA)
            }
        }
    }
}
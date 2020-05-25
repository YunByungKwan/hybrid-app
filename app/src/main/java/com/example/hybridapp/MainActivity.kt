package com.example.hybridapp

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var utils: Utils

    private var cameraAction: FlexAction? = null
    private var singlePhotoAction: FlexAction? = null
    private var multiplePhotosAction: FlexAction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        utils = Utils()

        flex_web_view.setBaseUrl("file:///android_asset")
        flex_web_view.loadUrl("file:///android_asset/html/test.html")
        flex_web_view.addFlexInterface(FlexInterface())
        WebView.setWebContentsDebuggingEnabled(true)
        /** Camera Interface */
        flex_web_view.setAction("Camera") { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                cameraAction = action

                if(utils.hasPermissionFor(Constants.PERM_CAMERA)) {

                    requestCameraIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_CAMERA)) {
                        val title = "알림"
                        val message = "앱을 사용하기 위해 카메라 권한이 필요합니다."
                        val positiveButtonText = "설정"
                        val negativeButtonText = "취소"
                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
                            clickListener, null)

                        cameraAction = null
                    } else {
                        utils.requestPermissions(arrayOf(Constants.PERM_CAMERA),
                            Constants.REQUEST_PERMISSIONS_CAMERA)

                    }
                }
            }
        }
        /** Gallery Interface */
        flex_web_view.setAction("SinglePhoto") { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                singlePhotoAction = action

                if(utils.hasPermissionFor(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                    && utils.hasPermissionFor(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                    requestSinglePhotoIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                        || utils.isRejectPermission(Constants.PERM_READ_EXTERNAL_STORAGE)) {
                        val title = "알림"
                        val message = "앱을 사용하기 위해 저장소 읽기/쓰기 권한이 필요합니다."
                        val positiveButtonText = "설정"
                        val negativeButtonText = "취소"
                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
                            clickListener, null)

                        singlePhotoAction = null
                    } else {
                        utils.requestPermissions(
                            arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                                Constants.PERM_READ_EXTERNAL_STORAGE),
                            Constants.REQUEST_SINGLE_PHOTO_INTENT)
                    }
                }
            }
        }
        flex_web_view.setAction("MultiplePhotos") { action, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                multiplePhotosAction = action

                if(utils.hasPermissionFor(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                    && utils.hasPermissionFor(Constants.PERM_READ_EXTERNAL_STORAGE)) {

                    requestMultiplePhotoIntent()

                } else {
                    if(utils.isRejectPermission(Constants.PERM_WRITE_EXTERNAL_STORAGE)
                        || utils.isRejectPermission(Constants.PERM_READ_EXTERNAL_STORAGE)) {
                        val title = "알림"
                        val message = "앱을 사용하기 위해 저장소 읽기/쓰기 권한이 필요합니다."
                        val positiveButtonText = "설정"
                        val negativeButtonText = "취소"
                        val clickListener = DialogInterface.OnClickListener { _, _ ->
                            startActivity(utils.takeAppSettingsIntent())
                        }

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
                            clickListener, null)

                        utils.showDialog(title, message, positiveButtonText, negativeButtonText,
                            clickListener, null)

                        multiplePhotosAction = null
                    } else {
                        utils.requestPermissions(
                            arrayOf(Constants.PERM_WRITE_EXTERNAL_STORAGE,
                                Constants.PERM_READ_EXTERNAL_STORAGE),
                        Constants.REQUEST_MULTIPLE_PHOTOS_INTENT)
                    }
                }
            }
        }
    }

    private fun requestCameraIntent() {
        val cameraIntent = utils.takeCameraIntent()

        if(utils.existsReceiveActivity(cameraIntent, packageManager)) {
            startActivityForResult(cameraIntent, Constants.REQUEST_CAMERA_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, "Camera app can't be launched.")

            cameraAction = null
        }
    }

    private fun requestSinglePhotoIntent() {
        val singlePhotoIntent = utils.takeSinglePhotoIntent()

        if(utils.existsReceiveActivity(singlePhotoIntent, packageManager)) {
            startActivityForResult(singlePhotoIntent, Constants.REQUEST_SINGLE_PHOTO_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, "Gallery app can't be launched.")

            singlePhotoAction = null
        }
    }

    private fun requestMultiplePhotoIntent() {
        val multiplePhotosIntent = utils.takeMultiplePhotosIntent()

        if(utils.existsReceiveActivity(multiplePhotosIntent, packageManager)) {
            startActivityForResult(multiplePhotosIntent,
                Constants.REQUEST_MULTIPLE_PHOTOS_INTENT)
        } else {
            Log.e(Constants.TAG_MAIN, "Gallery app can't be launched.")

            multiplePhotosAction = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Constants.REQUEST_PERMISSIONS_CAMERA) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.e(Constants.TAG_MAIN, "Camera permission is granted.")

                requestCameraIntent()

            } else {
                utils.showPermissionDeniedDialog()
            }
        } else if(requestCode == Constants.REQUEST_SINGLE_PHOTO_INTENT) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.e(Constants.TAG_MAIN, "READ/WRITE Storage permission is granted.")

                requestSinglePhotoIntent()

            } else {
                utils.showPermissionDeniedDialog()
            }
        } else if(requestCode == Constants.REQUEST_MULTIPLE_PHOTOS_INTENT) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.e(Constants.TAG_MAIN, "READ/WRITE Storage permission is granted.")

                requestMultiplePhotoIntent()

            } else {
                utils.showPermissionDeniedDialog()

                when {
                    cameraAction != null -> {
                        cameraAction!!.promiseReturn("Cancel")
                    }
                    singlePhotoAction != null -> {
                        singlePhotoAction!!.promiseReturn("Cancel")
                    }
                    multiplePhotosAction != null -> {
                        multiplePhotosAction!!.promiseReturn("Cancel")
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_CAMERA_INTENT -> {
                    Log.e(Constants.TAG_MAIN, "REQUEST_CAMERA_INTENT in onActivityResult()")

                    if(data != null) {
                        val imageBitmap = data.extras!!.get("data") as Bitmap
                        val base64Image = utils.convertBitmapToBase64(imageBitmap)

                        Log.e(Constants.TAG_MAIN, "base64: $base64Image")

                        cameraAction?.promiseReturn(base64Image)
                        cameraAction = null
                    }else {
                        Log.e(Constants.TAG_MAIN, "Data is null.")

                        cameraAction = null
                    }
                }
                Constants.REQUEST_SINGLE_PHOTO_INTENT -> {
                    Log.e(Constants.TAG_MAIN, "REQUEST_GALLERY_INTENT in onActivityResult()")

                    val imageUri = data?.data
                    if(imageUri != null) {
                        val base64Image = utils.convertUriToBase64(imageUri)

                        Log.e(Constants.TAG_MAIN, "base64: $base64Image")

                        singlePhotoAction?.promiseReturn(base64Image)
                        singlePhotoAction = null
                    }else {
                        Log.e(Constants.TAG_MAIN, "imageUri is null.")
                    }
                }
                Constants.REQUEST_MULTIPLE_PHOTOS_INTENT -> {
                    Log.e(Constants.TAG_MAIN,
                        "REQUEST_MULTIPLE_PHOTOS_INTENT in onActivityResult()")

                    val base64Photos = ArrayList<String>()
                    val clipData = data?.clipData

                    if(clipData != null) {
                        if(clipData.itemCount in 1..9) {
                            for(idx in 0 until clipData.itemCount) {
                                val imageUri = clipData.getItemAt(idx).uri
                                val base64Image = utils.convertUriToBase64(imageUri)

                                base64Photos.add(base64Image)
                            }

                            multiplePhotosAction!!.promiseReturn(base64Photos.toArray())
                            multiplePhotosAction = null
                        } else {
                            Log.e(Constants.TAG_MAIN, "10장 이상의 사진을 첨부할 수 없습니다.")
                        }
                    } else {
                        Log.e(Constants.TAG_MAIN, "ClipData is null.")
                    }
                }
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED) {
            Log.e(Constants.TAG_MAIN, "Activity.RESULT_CANCELED in onActivityResult()")
            singlePhotoAction?.promiseReturn("Cancel")
        } else {
            Log.e(Constants.TAG_MAIN, "Activity.RESULT_FAIL in onActivityResult()")
        }
    }
}

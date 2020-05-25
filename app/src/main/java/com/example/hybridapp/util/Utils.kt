package com.example.hybridapp.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hybridapp.App
import com.example.hybridapp.BuildConfig
import com.example.hybridapp.R
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class Utils {

    /** ###################################### Dialog ########################################### */

    /** 짧은 Toast 메시지 출력 */
    fun showShortToast(message: String) {
        funLOGE("showShortToast")

        val context = App.INSTANCE
        val duration = Toast.LENGTH_SHORT
        Toast.makeText(context, message, duration).show()
    }

    /** 긴 Toast 메시지 출력 */
    fun showLongToast(message: String) {
        funLOGE("showLongToast")

        val context = App.INSTANCE
        val duration = Toast.LENGTH_LONG
        Toast.makeText(context, message, duration).show()
    }

    /** 짧은 Snackbar 메시지 출력 */
    fun showShortSnackbar(view: View, message: String) {
        funLOGE("showShortSnackbar")

        val duration = Snackbar.LENGTH_SHORT
        Snackbar.make(view, message, duration).show()
    }

    /** 긴 Snackbar 메시지 출력 */
    fun showLongSnackbar(view: View, message: String) {
        funLOGE("showLongSnackbar")

        val duration = Snackbar.LENGTH_LONG
        Snackbar.make(view, message, duration).show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String) {
        funLOGE("showDialog")
        
        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .create()
            .show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String, positiveButtonText: String) {
        funLOGE("showDialog")

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, null)
            .create()
            .show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String,
                   positiveButtonText: String,
                   negativeButtonText: String) {
        funLOGE("showDialog")

        AlertDialog.Builder(App.activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, null)
            .setNegativeButton(negativeButtonText, null)
            .create()
            .show()
    }

    /** 다이얼로그 출력 */
    fun showDialog(title: String, message: String,
                           positiveButtonText: String,
                           negativeButtonText: String,
                           pListener: DialogInterface.OnClickListener?,
                           nListener: DialogInterface.OnClickListener?) {
        funLOGE("showDialog")

        AlertDialog.Builder(App.activity
        )
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, pListener)
            .setNegativeButton(negativeButtonText, nListener)
            .create()
            .show()
    }

    /** #################################### Permission ######################################### */

    /** 해당 권한이 있는지 확인 */
    internal fun hasPermissionFor(permissionName: String): Boolean
            = (ContextCompat.checkSelfPermission(App.INSTANCE, permissionName)
                    == PackageManager.PERMISSION_GRANTED)

    /** 해당 권한이 거절된 적이 있는지 확인 */
    internal fun isRejectPermission(permissionName: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(App.activity, permissionName)

    /** 다수 권한 요청 */
    fun requestPermissions(permissions: Array<out String>, requestCode: Int) {
        funLOGE("requestPermissions")

        ActivityCompat.requestPermissions(App.activity, permissions, requestCode)
    }

    /** 권한 거부 시 다이얼로그 */
    fun showPermissionDeniedDialog() {
        val title: String = App.INSTANCE.getString(R.string.permission_denied_dialog_title)
        val message = App.INSTANCE.getString(R.string.permission_denied_dialog_message)
        val positiveButtonText
                = App.INSTANCE.getString(R.string.permission_denied_dialog_positiveButtonText)

        showDialog(title, message, positiveButtonText)
    }

    /** 앱 설정 인텐트 가져오기 */
    fun takeAppSettingsIntent(): Intent {
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        settingsIntent.data = Uri.parse("package:"+ BuildConfig.APPLICATION_ID)
        settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return settingsIntent
    }

    /** ######################################### Network ####################################### */

    /**
     * activeNetworkInfo is deprecated in API level 29(Q)
     *
     * activeNetwork is added in API level 23(M)
     * getNetworkCapabilities() is added in API level 21(L)
     * hasTransport() is added in API level 21(L)
     */
    fun isNetworkConnected(): Boolean {
        funLOGE("isNetworkConnected")

        val connectivityManger
                = App.activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(Constants.TAG_NETWORK, "Build version is greater than Marshmallow.")

            val activeNetwork = connectivityManger.activeNetwork ?: return false
            val networkCapabilities
                    = connectivityManger.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.e(Constants.TAG_NETWORK, "")

                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            Log.e(Constants.TAG_NETWORK, "Build version is smaller than Marshmallow.")

            connectivityManger.activeNetworkInfo ?: return false
        }

        return true
    }

    /** ######################################## Photo ########################################## */

    /** 카메라 인텐트 가져오기 */
    fun takeCameraIntent(): Intent {
        funLOGE("takeCameraIntent")

        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    /** 갤러리 인텐트 가져오기 */
    fun takeSinglePhotoIntent(): Intent {
        funLOGE("takeSinglePhotoIntent")

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"

        return galleryIntent
    }

    /** 갤러리 인텐트 가져오기(여러 장) */
    fun takeMultiplePhotosIntent(): Intent {
        funLOGE("takeMultiplePhotosIntent")

        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        return galleryIntent
    }

    /** 암시적 인텐트를 받을 수 있는 앱이 있는지 확인 */
    fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean {
        funLOGE("existsReceiveActivity")

        return intent.resolveActivity(packageManager) != null
    }

    /** Uri->Base64 로 변환 */
    internal fun convertUriToBase64(uri: Uri): String {
        funLOGE("convertUriToBase64")

        val bitmap = convertUriToBitmap(uri)

        return convertBitmapToBase64(bitmap)
    }

    /** Uri->Bitmap 으로 변환 */
    private fun convertUriToBitmap(uri: Uri): Bitmap {
        funLOGE("convertUriToBitmap")

        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val source
                    = ImageDecoder.createSource(App.INSTANCE.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(App.INSTANCE.contentResolver, uri)
        }
    }

    /** Bitmap->Base64 로 변환 */
    internal fun convertBitmapToBase64(bitmap: Bitmap): String {
        funLOGE("convertBitmapToBase64")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /** log.e wrapper function */
    private fun funLOGE(functionName: String) {
        Log.e(Constants.TAG_UTILS, "call $functionName() in ${Constants.TAG_UTILS}")
    }
}
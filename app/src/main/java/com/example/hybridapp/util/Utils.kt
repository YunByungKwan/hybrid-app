package com.example.hybridapp.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.module.Dialog
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object Utils {

    /** permissionName이 있는지 없는지 확인 */
    fun existsPermission(permissionName: String): Boolean
            = (ContextCompat.checkSelfPermission(App.INSTANCE, permissionName)
            == PackageManager.PERMISSION_GRANTED)

    /** permissions가 모두 있는지 확인 */
    fun existAllPermission(permissions: Array<out String>): Boolean {
        for(permissionName in permissions) {
            if(!existsPermission(permissionName)) {
                return false
            }
        }

        return true
    }

    /** permissionName이 거절되었는지 확인 */
    internal fun isDenialPermission(permissionName: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(App.activity, permissionName)

    /** 위험 권한이 없을 경우 */
    fun checkDangerousPermissions(permissions: Array<out String>, permissionCode: Int) {
        if (existsDenialPermission(permissions)) {
            Dialog.show(Constants.DIAL_TITLE,
                getDialogMessage(permissionCode),
                Constants.DIAL_POS,
                null,
                Constants.DIAL_NEG,
                DialogInterface.OnClickListener { _, _ ->
                    requestAppSettingsIntent()
                },
                null,
                null,
                {})
        } else {
            val permissionsToRequest = getPermissionsToRequest(permissions)
            requestPermissions(permissionsToRequest, permissionCode)
        }
    }

    /** 거절한 권한이 하나라도 있는지 확인 */
    private fun existsDenialPermission(permissions: Array<out String>): Boolean {
        for(permissionName in permissions) {
            if(isDenialPermission(permissionName)) {
                return true
            }
        }
        return false
    }

    /** 요청이 필요한 권한들을 반환 */
    private fun getPermissionsToRequest(permissions: Array<out String>): Array<out String> {
        val permissionsToRequest = ArrayList<String>()

        for(permissionName in permissions) {
            if(!existsPermission(permissionName)) {
                permissionsToRequest.add(permissionName)
            }
        }

        return permissionsToRequest.toTypedArray()
    }

    /** 다수 권한 요청 */
    fun requestPermissions(permissions: Array<out String>, requestCode: Int) {
        Constants.LOGE("requestPermissions", Constants.TAG_UTILS)

        ActivityCompat.requestPermissions(App.activity, permissions, requestCode)
    }

    /** 앱 설정 화면으로 이동 */
    private fun requestAppSettingsIntent() {
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        settingsIntent.data = Uri.fromParts("package", App.INSTANCE.packageName, null)
        ActivityCompat.startActivity(App.activity, settingsIntent, null)
    }

    /** 권한 다이얼로그 메시지 */
    private fun getDialogMessage(permissionCode: Int): String {
        when(permissionCode) {
            Constants.REQ_PERM_CODE_CAMERA -> {
                return Constants.DIAL_MSG_CAMERA
            }
            Constants.REQ_PERM_CODE_WRITE -> {
                return Constants.DIAL_MSG_WRITE
            }
            Constants.REQ_PERM_CODE_READ_WRITE -> {
                return Constants.DIAL_MSG_READ_WRITE
            }
            Constants.REQ_PERM_CODE_LOCATION -> {
                return Constants.DIAL_MSG_LOCATION
            }
            Constants.REQ_PERM_CODE_RECORD_AUDIO -> {
                return Constants.DIAL_MSG_RECORD_AUDIO
            }
            Constants.REQ_PERM_CODE_SEND_SMS -> {
                return Constants.DIAL_MSG_SEND_SMS
            }
        }

        return ""
    }

    /** 암시적 인텐트를 받을 수 있는 앱이 있는지 확인 */
    fun existsReceiveActivity(intent: Intent, packageManager: PackageManager): Boolean
            = (intent.resolveActivity(packageManager) != null)

    fun getAppSignatures(context: Context): ArrayList<String> {
        val appCodes: ArrayList<String> = ArrayList()

        try {
            val packageName: String = context.packageName
            val packageManager: PackageManager = context.packageManager
            val signatures: Array<Signature> =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures

            for (signature in signatures) {
                val hash = getHash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }
                Log.e(Constants.TAG_UTILS,
                    String.format("이 값을 SMS 뒤에 써서 보내주면 됩니다 : %s", hash)
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(Constants.TAG_UTILS, "Unable to find package to obtain hash. : $e")
        }

        return appCodes
    }

    /** Hash값 반환 */
    private fun getHash(packageName: String, signature: String): String? {
        Constants.LOGE("getHash", Constants.TAG_UTILS)
        val appInfo = "$packageName $signature"

        try {
            val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            }
            var hashSignature: ByteArray = messageDigest.digest()

            hashSignature = hashSignature.copyOfRange(0, 9)

            var base64Hash = encode11DigitsBase64String(hashSignature)

            Log.e(Constants.TAG_UTILS,
                String.format("\nPackage : %s\nHash : %s", packageName, base64Hash))

            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(Constants.TAG_UTILS, "hash:NoSuchAlgorithm : $e")
        }

        return null
    }

    /** 11자리 base64로 인코딩 */
    private fun encode11DigitsBase64String(hashSignature: ByteArray): String {
        return Base64.encodeToString(hashSignature,
            Base64.NO_PADDING or Base64.NO_WRAP).substring(0, 11)
    }

    /** Instance id 반환 */
    fun getInstanceId(): String {
        var instanceId = ""
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {task ->
            if(task.isSuccessful) {
                val token = task.result?.token

                if(token != null) {
                    instanceId = token
                }
            } else {
                Log.e(Constants.TAG_IDENTIFIER, "getInstanceId failed", task.exception)
            }
        }

        return instanceId
    }

    /** Guid 반환 */
    fun getGUID(): String {
        return UUID.randomUUID().toString()
    }

    /** 현재 날짜와 시간 반환 */
    fun getCurrentDateAndTime(): String {
        val currentDateTime = Calendar.getInstance().time
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)
    }

    /** Inflater 가져오기 */
    fun getLayoutInflater(context: Context): LayoutInflater {
        return context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /** 동적 뷰 생성 */
    fun createView(resource: Int, view: ViewGroup?, attachToRoot: Boolean) {
        val mInflater = (App.INSTANCE).getSystemService(
            Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mInflater.inflate(resource, view, attachToRoot)
    }

    /** 화면 크기 정보 가져오기 */
    fun getScreenSize(context: Context): Map<String, Int> {
        val displayMetrics = DisplayMetrics()
        App.activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        return mapOf(Constants.SCREEN_WIDTH to width, Constants.SCREEN_HEIGHT to height)
    }

    /** 닫기 버튼 동적 생성 */
    fun createCloseButton(context: Context): Button {
        val button = Button(context)
        val params = ConstraintLayout.LayoutParams(70, 70)
        params.topToTop = R.id.constraintLayout
        params.endToEnd = R.id.constraintLayout
        params.startToStart = R.id.constraintLayout
        params.topMargin = 10
        button.text = "X"
        button.background = ContextCompat.getDrawable(context, R.drawable.circle)
        button.textAlignment = View.TEXT_ALIGNMENT_CENTER

        return button
    }

    fun getParamsAlignCenterInConstraintLayout(
        width: Int, height: Int, root: Int): ConstraintLayout.LayoutParams {
        val params = getParamsInConstraintLayout(width, height)
        params.topToTop = root
        params.bottomToBottom = root
        params.endToEnd = root
        params.startToStart = root

        return params
    }

    fun getParamsInConstraintLayout(width: Int, height: Int): ConstraintLayout.LayoutParams {
        return ConstraintLayout.LayoutParams(width, height)
    }
}
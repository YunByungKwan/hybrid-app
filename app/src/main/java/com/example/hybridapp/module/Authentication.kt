package com.example.hybridapp.module

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import app.dvkyun.flexhybridand.FlexLambda
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

object Authentication {

    var authAction: FlexAction? = null

    /**======================================= Action ============================================*/
    @RequiresApi(Build.VERSION_CODES.P)
    val authentication
            = FlexLambda.action { action, _->
        withContext(Main) {
            val fragmentActivity = App.activity as FragmentActivity
            authAction = action

            if(canAuthenticate()) {
                showPrompt(fragmentActivity)
            } else {
                val returnObj = Utils.returnJson(true, false, "인증을 진행할 수 없습니다")
                authAction?.promiseReturn(returnObj)
            }
        }
    }

    /** 생체 인증이 가능한지 판별 */
    private fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(App.INSTANCE)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Utils.LOGD(App.context.getString(R.string.biometric_success))
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Utils.LOGD(App.context.getString(R.string.biometric_error_no_hardware))
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Utils.LOGD(App.context.getString(R.string.biometric_error_hw_unavailable))
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Utils.LOGD(App.context.getString(R.string.biometric_error_none_enrolled))
                return false
            }
        }
        return false
    }

    /** 생체 인증 다이얼로그 띄우기 */
    private fun showPrompt(fragmentActivity: FragmentActivity){
        val promptInfo = getBiometricPromptInfo()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val biometricPrompt = BiometricPrompt(fragmentActivity,
                App.INSTANCE.mainExecutor, getAuthenticationCallback())
            biometricPrompt.authenticate(promptInfo)
        } else {
            val returnObj = Utils.returnJson(
                authValue = true,
                dataValue = false, msgValue = "인증을 진행할 수 없습니다"
            )
            authAction?.promiseReturn(returnObj)
        }
    }

    private fun getBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
        .setTitle(App.context.getString(R.string.bio_prompt_title))
        .setDescription(App.context.getString(R.string.bio_prompt_description))
        .setSubtitle(App.context.getString(R.string.bio_prompt_sub_title))
        .setNegativeButtonText(App.context.getString(R.string.bio_prompt_negative_button))
        .build()
    }

    /** Authentication Callback 객체 */
    private fun getAuthenticationCallback() = object : BiometricPrompt.AuthenticationCallback() {

        // 인증 성공시
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            val returnObj = Utils.returnJson(
                authValue = true,
                dataValue = true, msgValue = null
            )
            authAction?.promiseReturn(returnObj)
        }

        // 인증 취소 버튼 클릭시, 인증에 실패시
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            val returnObj = Utils.returnJson(
                authValue = true,
                dataValue = false, msgValue = "인증에 실패했습니다"
            )
            authAction?.promiseReturn(returnObj)
        }

        // 인증이 일치하지 않을 경우, 여러번 틀렸을 경우
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
        }
    }
}
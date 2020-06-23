package com.example.hybridapp.util.module

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import com.example.hybridapp.util.Utils

object Authentication {

    /** 생체 인증이 가능한지 판별 */
    fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(App.INSTANCE)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Constants.LOGD(Constants.BIOMETRIC_SUCCESS)
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Constants.LOGD(Constants.BIOMETRIC_ERROR_NO_HARDWARE)
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Constants.LOGD(Constants.BIOMETRIC_ERROR_HW_UNAVAILABLE)
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Constants.LOGD(Constants.BIOMETRIC_ERROR_NONE_ENROLLED)
                return false
            }
        }
        return false
    }

    /** 생체 인증 다이얼로그 띄우기 */
    fun showPrompt(fragmentActivity: FragmentActivity){
        Constants.LOGD("Call showPrompt()")

        val promptInfo = getBiometricPromptInfo()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val biometricPrompt = BiometricPrompt(fragmentActivity,
                App.INSTANCE.mainExecutor, getAuthenticationCallback())
            biometricPrompt.authenticate(promptInfo)
        } else {
            val returnObj = Utils.createJSONObject(
                authValue = true,
                dataValue = false, msgValue = "인증을 진행할 수 없습니다"
            )
            (App.activity as BasicActivity).authAction?.promiseReturn(returnObj)
        }
    }

    private fun getBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
        .setTitle(Constants.BIO_PROMPT_TITLE)
        .setDescription(Constants.BIO_PROMPT_DESCRIPTION)
        .setSubtitle(Constants.BIO_PROMPT_SUB_TITLE)
        .setNegativeButtonText(Constants.BIO_PROMPT_NEGATIVE_BUTTON)
        .build()
    }

    /** Authentication Callback 객체 */
    private fun getAuthenticationCallback() = object : BiometricPrompt.AuthenticationCallback() {

        // 인증 성공시
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            Constants.LOGD("Call onAuthenticationSucceeded() in AuthenticationCallback()")
            val returnObj = Utils.createJSONObject(
                authValue = true,
                dataValue = true, msgValue = null
            )
            (App.activity as BasicActivity).authAction?.promiseReturn(returnObj)
        }

        // 인증 취소 버튼 클릭시, 인증에 실패시
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            Constants.LOGE("Call onAuthenticationError() in AuthenticationCallback()")
            val returnObj = Utils.createJSONObject(
                authValue = true,
                dataValue = false, msgValue = "인증에 실패했습니다"
            )
            (App.activity as BasicActivity).authAction?.promiseReturn(returnObj)
        }

        // 인증이 일치하지 않을 경우, 여러번 틀렸을 경우
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Constants.LOGE("Call onAuthenticationFailed() in AuthenticationCallback()")
        }
    }
}
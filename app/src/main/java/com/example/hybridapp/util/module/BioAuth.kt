package com.example.hybridapp.util.module

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import app.dvkyun.flexhybridand.FlexAction
import com.example.hybridapp.App
import com.example.hybridapp.basic.BasicActivity
import com.example.hybridapp.util.Constants
import java.util.concurrent.Executor

object BioAuth {

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
    fun showPrompt(fragmentActivity: FragmentActivity, action: FlexAction?){
        Constants.LOGD("Call showPrompt()")

        (App.activity as BasicActivity).bioAuthAction = action

        val promptInfo = getBiometricPromptInfo()

        val biometricPrompt = BiometricPrompt(fragmentActivity,
            Executor {}, getAuthenticationCallback())

        biometricPrompt.authenticate(promptInfo)
    }

    private fun getBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
        .setTitle(Constants.BIO_PROMPT_TITLE)
        .setDescription(Constants.BIO_PROMPT_DESCRIPTION)
        .setSubtitle(Constants.BIO_PROMPT_SUB_TITLE)
        .setNegativeButtonText(Constants.BIO_PROMPT_NEGATIVE_BUTTON)
        .build()
    }

    private fun getAuthenticationCallback() = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            Constants.LOGE("Call onAuthenticationError()")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            Constants.LOGD("Call onAuthenticationSucceeded()")

            val cryptoObject = result.cryptoObject
            if(cryptoObject != null) {
                val cipher = cryptoObject.cipher
                Constants.LOGD("Cipher: $cipher")
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Constants.LOGE("Call onAuthenticationFailed()")
        }
    }
}
package com.example.hybridapp.util.module

import android.util.Log
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
                Log.e(Constants.TAG_BIO_AUTH, Constants.BIOMETRIC_SUCCESS)

                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(Constants.TAG_BIO_AUTH, Constants.BIOMETRIC_ERROR_NO_HARDWARE)

                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(Constants.TAG_BIO_AUTH, Constants.BIOMETRIC_ERROR_HW_UNAVAILABLE)

                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(Constants.TAG_BIO_AUTH, Constants.BIOMETRIC_ERROR_NONE_ENROLLED)

                return false
            }
        }
        return false
    }

    /** 생체 인증 다이얼로그 띄우기 */
    fun showPrompt(fragmentActivity: FragmentActivity, action: FlexAction?){
        Constants.LOGE("showPrompt", Constants.TAG_BIO_AUTH)

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

            Constants.LOGE("onAuthenticationError", Constants.TAG_BIO_AUTH)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)

            Constants.LOGE("onAuthenticationSucceeded", Constants.TAG_BIO_AUTH)

            val cryptoObject = result.cryptoObject
            if(cryptoObject != null) {
                val cipher = cryptoObject.cipher
                Log.e(Constants.TAG_BIO_AUTH, "cipher: $cipher")
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Constants.LOGE("onAuthenticationFailed", Constants.TAG_BIO_AUTH)
        }
    }
}
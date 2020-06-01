package com.example.hybridapp.util.module

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.example.hybridapp.App
import com.example.hybridapp.R
import com.example.hybridapp.util.Constants
import java.util.concurrent.Executor

object BioAuth {

    fun showPrompt(fragmentActivity: FragmentActivity){
        Constants.LOGE("showPrompt", Constants.TAG_BIO_AUTH)

        var biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(App.INSTANCE.getString(R.string.biometricPrompt_title))
            .setDescription(App.INSTANCE.getString(R.string.biometricPrompt_description))
            .setSubtitle(App.INSTANCE.getString(R.string.biometricPrompt_sub_title))
            .setNegativeButtonText(App.INSTANCE.getString(R.string.biometricPrompt_negative_button))
            .build()

        val authenticationCallback = getAuthenticationCallback()
        val biometricPrompt = BiometricPrompt(fragmentActivity, Executor {  }, authenticationCallback)

        biometricPrompt.authenticate(biometricPromptInfo)
    }

    private fun getAuthenticationCallback() = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            Constants.LOGE("onAuthenticationError", Constants.TAG_BIO_AUTH)

            //super.onAuthenticationError(errorCode, errString)


        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            Constants.LOGE("onAuthenticationSucceeded", Constants.TAG_BIO_AUTH)

            // super.onAuthenticationSucceeded(result)
        }

        override fun onAuthenticationFailed() {
            Constants.LOGE("onAuthenticationFailed", Constants.TAG_BIO_AUTH)

            //super.onAuthenticationFailed()
        }
    }
}
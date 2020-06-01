package com.example.hybridapp.util.module

import android.content.Intent
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.example.hybridapp.App
import com.example.hybridapp.util.Constants

object Record {

    /** Get intent */
    fun getIntent() {
        val recordIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        ActivityCompat.startActivityForResult(App.activity, recordIntent,
            Constants.REQ_CODE_RECORD, null
        )
    }
}
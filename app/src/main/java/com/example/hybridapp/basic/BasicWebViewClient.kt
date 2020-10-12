package com.example.hybridapp.basic

import android.webkit.WebView
import app.dvkyun.flexhybridand.FlexWebViewClient
import com.example.hybridapp.App
import com.example.hybridapp.data.LogUrl
import com.example.hybridapp.data.LogUrlRepository
import com.example.hybridapp.data.LogUrlRoomDatabase
import com.example.hybridapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BasicWebViewClient: FlexWebViewClient() {

    /** 웹뷰 url 변경시 호출됨 */
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        CoroutineScope(Dispatchers.Default).launch {
            val currentDateTime = Utils.getCurrentDateAndTime()

            if(url != null) {
                val logUrlDao = LogUrlRoomDatabase.getDatabase(App.INSTANCE).logUrlDao()
                val repository = LogUrlRepository(logUrlDao)
                val logUrl = LogUrl(0, currentDateTime, url)
                repository.insert(logUrl)
            }
        }

        super.doUpdateVisitedHistory(view, url, isReload)
    }
}
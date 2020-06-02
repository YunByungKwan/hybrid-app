package com.example.hybridapp.data

import com.example.hybridapp.data.LogUrl
import com.example.hybridapp.data.LogUrlDao

class LogUrlRepository(private val logUrlDao: LogUrlDao) {

    val allLogUrls: List<LogUrl> = logUrlDao.getAll()

    suspend fun insert(logUrl: LogUrl) {
        logUrlDao.insertAll(logUrl)
    }
}
package com.example.hybridapp.data

class LogUrlRepository(private val logUrlDao: LogUrlDao) {

    val allLogUrls: List<LogUrl> = logUrlDao.getAll()

    suspend fun insert(logUrl: LogUrl) {
        logUrlDao.insertAll(logUrl)
    }
}
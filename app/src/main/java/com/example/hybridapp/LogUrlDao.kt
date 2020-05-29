package com.example.hybridapp

import androidx.room.*

@Dao
interface LogUrlDao {

    @Query("SELECT * FROM url_log")
    fun getAll(): List<LogUrl>

    // vararg: 가변인자
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg logUrl: LogUrl)

}
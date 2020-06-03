package com.example.hybridapp.data

import androidx.room.*
import com.example.hybridapp.data.LogUrl

@Dao
interface LogUrlDao {

    @Query("SELECT * FROM url_log ORDER BY visitingTime DESC")
    fun getAll(): List<LogUrl>

    // vararg: 가변인자
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg logUrl: LogUrl)

}
package com.example.hybridapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "url_log")
data class LogUrl(

    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "visitingTime")
    var visitingTime: String,

    @ColumnInfo(name = "visitingUrl")
    var visitingUrl: String

)
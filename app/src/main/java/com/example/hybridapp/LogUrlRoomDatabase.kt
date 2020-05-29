package com.example.hybridapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LogUrl::class], version = 1)
abstract class LogUrlRoomDatabase: RoomDatabase() {

    abstract fun logUrlDao(): LogUrlDao

    companion object {
        @Volatile
        private var INSTANCE: LogUrlRoomDatabase? = null

        fun getDatabase(context: Context): LogUrlRoomDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogUrlRoomDatabase::class.java,
                    "url_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
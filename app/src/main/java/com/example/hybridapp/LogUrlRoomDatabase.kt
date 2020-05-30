package com.example.hybridapp

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LogUrl::class], version = 1)
abstract class LogUrlRoomDatabase: RoomDatabase() {

    abstract fun logUrlDao(): LogUrlDao

    companion object {
        @Volatile
        private var INSTANCE: LogUrlRoomDatabase? = null
        private const val TAG = "LogUrlRoomDatabase"

        fun getDatabase(context: Context): LogUrlRoomDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) {
                Log.e(TAG, "Database is already created.")

                return tempInstance
            }

            synchronized(LogUrlRoomDatabase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogUrlRoomDatabase::class.java,
                    "url_database.db"
                ).build()

                INSTANCE = instance

                Log.e(TAG, "Database create.")

                return instance
            }
        }
    }
}
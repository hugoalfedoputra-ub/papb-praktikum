package com.example.ch2p.data.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tugas::class], version = 1)
abstract class TugasDatabase : RoomDatabase() {
    abstract fun tugasDao(): TugasDao

    companion object {
        @Volatile
        private var INSTANCE: TugasDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): TugasDatabase {
            if (INSTANCE == null) {
                synchronized(TugasDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, TugasDatabase::class.java, "tugas_database"
                    ).build()
                }
            }
            return INSTANCE as TugasDatabase
        }
    }
}
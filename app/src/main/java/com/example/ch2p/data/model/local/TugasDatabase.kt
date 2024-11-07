package com.example.ch2p.data.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Tugas::class], version = 3, exportSchema = false)
@TypeConverters(UriConverter::class)
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
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE as TugasDatabase
        }
    }
}
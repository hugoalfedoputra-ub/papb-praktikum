package com.example.ch2p.data.model.local

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TugasRepository(application: Application) {
    private val tugasDao: TugasDao
    private val executorService: ExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        val db = TugasDatabase.getDatabase(application)
        tugasDao = db.tugasDao()
    }

    fun getAllTugas(): LiveData<List<Tugas>> = tugasDao.getAll()

    fun insertTugas(tugas: Tugas) {
        executorService.execute { tugasDao.insertTugas(tugas) }
    }

    fun markAsDone(id: Int, state: Boolean){
        executorService.execute { tugasDao.markAsDone(id, state) }
    }


    companion object {
        private const val TAG = "Tugas Repository"

        @Volatile
        private var instance: TugasRepository? = null

        fun getInstance(
            application: Application
        ): TugasRepository = instance ?: synchronized(this) {
            instance ?: TugasRepository(application)
        }.also { instance = it }
    }
}
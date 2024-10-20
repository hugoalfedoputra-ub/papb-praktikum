package com.example.ch2p.data.model.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TugasDao {
    @Query("SELECT * FROM Tugas")
    fun getAll(): LiveData<List<Tugas>>

    @Query("UPDATE Tugas SET is_done=:state WHERE id = :id")
    fun markAsDone(id: Int, state: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTugas(tugas: Tugas)
}
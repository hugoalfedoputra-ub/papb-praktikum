package com.example.ch2p

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Matkul(
    @PropertyName("id") val id: String = "",
    @PropertyName("hari") val hari: String = "",
    @PropertyName("jam_mulai") val jamMulai: String = "",
    @PropertyName("jam_selesai") val jamSelesai: String = "",
    @PropertyName("kelas") val kelas: String = "",
    @PropertyName("nama") val nama: String = "",
    @PropertyName("praktikum") val praktikum: Boolean = false
)

class ListViewModel : ViewModel() {
    private val _schedule = MutableStateFlow<List<Matkul>>(emptyList())
    val schedule: StateFlow<List<Matkul>> = _schedule.asStateFlow()

    init {
        getSchedule()
    }

    private fun getSchedule() {
        viewModelScope.launch { // Use viewModelScope for coroutines
            try {
                val documents = Firebase.firestore.collection("matkul").get().await() // Use await()
                // Map documents to Matkul objects
                val matkulList = documents.map { document ->
                    Matkul(
                        id = document.id,
                        hari = document.getString("hari") ?: "",
                        jamMulai = document.getString("jam_mulai") ?: "",
                        jamSelesai = document.getString("jam_selesai") ?: "",
                        kelas = document.getString("kelas") ?: "",
                        nama = document.getString("nama") ?: "",
                        praktikum = document.getBoolean("praktikum") ?: false
                    )
                }
                _schedule.value = matkulList // Update the StateFlow
            } catch (e: Exception) {
                Log.w("ListViewModel", "Error getting documents: ", e)
            }
        }
    }
}
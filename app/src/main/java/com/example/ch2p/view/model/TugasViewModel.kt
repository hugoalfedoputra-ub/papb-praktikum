package com.example.ch2p.view.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ch2p.data.model.local.TugasRepository


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ch2p.data.model.local.Tugas
import com.example.ch2p.data.model.local.TugasDatabase
import kotlinx.coroutines.launch

class ViewModelFactory private constructor(private val tugasRepository: TugasRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TugasViewModel::class.java) -> TugasViewModel(
                TugasRepository
            ) as T

            else -> throw IllegalAccessException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}


class TugasViewModel(
    private val TugasRepository: TugasRepository.Companion,
) : ViewModel() {
    private val tugasRepository = TugasRepository(application = Application())

    private val _tugasList = tugasRepository.getAllTugas()
    val tugasList: LiveData<List<Tugas>> get() = _tugasList

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean?>()
    val isLoading: LiveData<Boolean?> get() = _isLoading

    init {
        getAllTugas()
    }

    private fun getAllTugas() {
        viewModelScope.launch {
            tugasRepository.getAllTugas()
        }
    }

    fun insertTugas(tugas: Tugas) {
        viewModelScope.launch {
            tugasRepository.insertTugas(tugas)
        }
    }

    fun markAsDone(id: Int, state: Boolean){
        viewModelScope.launch {
            tugasRepository.markAsDone(id, state)
        }
    }

}

object Injection {

    fun provideRepository(context: Context): TugasRepository {
        val database = TugasDatabase.getDatabase(context)
        return TugasRepository(application = Application())
    }
}

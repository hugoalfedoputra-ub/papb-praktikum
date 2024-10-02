package com.example.ch2p

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch2p.data.api.ApiConfig
import com.example.ch2p.data.api.ApiService
import com.example.ch2p.data.entity.Profile
import com.example.ch2p.data.entity.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {
    private val _userDetail = MutableStateFlow<Profile?>(null)
    private val _repoDetail = MutableStateFlow<List<Repo?>>(emptyList<Repo>())
    private val apiService: ApiService by lazy { ApiConfig.getApiService() }
    val userDetail: StateFlow<Profile?> = _userDetail.asStateFlow()
    val repoDetail: StateFlow<List<Repo?>> = _repoDetail.asStateFlow()

    fun getUserDetail(username: String) {
        viewModelScope.launch {
            apiService.getUserDetail(username).enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful) {
                        _userDetail.value = response.body()
                    } else {
                        Log.e(
                            "ProfileViewModel",
                            "Error: ${response.code()} - ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    Log.e("ProfileViewModel", "Error getting user detail: ", t)
                }
            })
        }
    }

    fun getUserRepoDetail(username: String) {
        viewModelScope.launch {
            apiService.getUserRepoDetail(username).enqueue(object : Callback<List<Repo>> {
                override fun onResponse(
                    call: Call<List<Repo>>,
                    response: Response<List<Repo>>
                ) {
                    if (response.isSuccessful) {
                        _repoDetail.value = response.body()!!
                    } else {
                        Log.e("ProfileViewModel", "Repo Error: ${response.code()} - ${response.message()}")
                        _repoDetail.value = emptyList() // You might want to handle error cases differently
                    }
                }

                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                    Log.e("ProfileViewModel", "Error getting user's repo detail: ", t)
                    // Handle the failure, maybe show an error message or retry
                }
            })
        }
    }
}
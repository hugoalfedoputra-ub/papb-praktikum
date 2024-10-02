package com.example.ch2p.data.api

import com.example.ch2p.data.entity.Profile
import com.example.ch2p.data.entity.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{username}")
    fun getUserDetail(@Path("username") username: String): Call<Profile>

    @GET("users/{username}/repos")
    fun getUserRepoDetail(@Path("username") username: String): Call<List<Repo>>
}
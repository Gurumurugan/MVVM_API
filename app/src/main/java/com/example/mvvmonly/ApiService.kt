package com.example.mvvmonly

import retrofit2.http.GET
import kotlinx.coroutines.flow.Flow

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>
}

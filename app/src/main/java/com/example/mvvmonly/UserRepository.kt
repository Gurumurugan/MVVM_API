package com.example.mvvmonly

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository(private val apiService: ApiService) {
    fun getUsers(): Flow<List<User>> = flow {
        emit(apiService.getUsers()) // Emits API response
    }
}

package com.example.androidplayground.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidplayground.data.AppDatabase
import com.example.androidplayground.data.repository.AuthRepository
import com.example.androidplayground.data.repository.AuthRepositoryImpl

class AuthViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repository: AuthRepository = AuthRepositoryImpl(database.userDao())
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
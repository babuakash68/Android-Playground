package com.example.androidplayground.data.repository

import com.example.androidplayground.data.User
import com.example.androidplayground.data.UserDao
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User>
    fun getLoggedInUser(): Flow<User?>
    suspend fun logout()
}

class AuthRepositoryImpl(private val userDao: UserDao) : AuthRepository {
    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val email = account.email ?: return Result.failure(IllegalArgumentException("Email is required"))
            val user = User(
                email = email,
                name = account.displayName ?: "User",
                photoUrl = account.photoUrl?.toString()
            )
            userDao.insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLoggedInUser(): Flow<User?> {
        return userDao.getLoggedInUser()
    }

    override suspend fun logout() {
        userDao.logoutAllUsers()
    }
} 
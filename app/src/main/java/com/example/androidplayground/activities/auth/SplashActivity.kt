package com.example.androidplayground.activities.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidplayground.R
import com.example.androidplayground.data.AppDatabase
import com.example.androidplayground.activities.dashboard.DashboardActivity
import com.example.androidplayground.activities.auth.MainActivity
import com.example.androidplayground.data.User
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        database = AppDatabase.getDatabase(this)

        // Check login status and navigate accordingly
        lifecycleScope.launch {
            database.userDao().getLoggedInUser().collect { user ->
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = if (user != null) {
                        // User is logged in, go to Dashboard with user data
                        Intent(this@SplashActivity, DashboardActivity::class.java).apply {
                            putExtra("user", user)
                        }
                    } else {
                        // User is not logged in, go to MainActivity (login screen)
                        Intent(this@SplashActivity, MainActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }, 2000) // 2 seconds delay for splash screen
            }
        }
    }
}
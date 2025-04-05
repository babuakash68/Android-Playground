package com.example.androidplayground.activities.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidplayground.R
import com.example.androidplayground.activities.auth.MainActivity
import com.example.androidplayground.activities.learning.ActivityTheoryActivity
import com.example.androidplayground.activities.learning.interactive.LifecycleInteractiveActivity
import com.example.androidplayground.data.AppDatabase
import com.example.androidplayground.data.model.AndroidTopic
import com.example.androidplayground.ui.adapter.AndroidTopicAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var topicsAdapter: AndroidTopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        database = AppDatabase.getDatabase(this)

        // Get user info from intent or database
        val userEmail = intent.getStringExtra("user_email") ?: ""
        val userName = intent.getStringExtra("user_name") ?: ""
        val userPhotoUrl = intent.getStringExtra("user_photo_url") ?: ""

        // If user data is not in intent, load from database
        if (userEmail.isEmpty()) {
            loadUserFromDatabase()
        } else {
            // Display user information from intent
            displayUserInfo(userEmail, userName, userPhotoUrl)
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.topicsRecyclerView)
        topicsAdapter = AndroidTopicAdapter(this) { topic ->
            // Handle topic click
            when (topic.title) {
                "Activity Lifecycle" -> {
                    val intent = Intent(this, ActivityTheoryActivity::class.java)
                    intent.putExtra("topic_title", topic.title)
                    intent.putExtra("topic_description", topic.description)
                    startActivity(intent)
                }
                else -> {
                    // TODO: Handle other topics
                }
            }
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = topicsAdapter
        }

        // Load topics
        loadTopics()

        // Handle logout
        findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            lifecycleScope.launch {
                // Update user login status in database
                database.userDao().updateLoginStatus(userEmail, false)
                
                // Sign out from Google Sign-In
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    .requestProfile()
                    .build()
                
                val googleSignInClient = GoogleSignIn.getClient(this@DashboardActivity, gso)
                googleSignInClient.signOut().addOnCompleteListener {
                    // Return to login screen
                    startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun loadUserFromDatabase() {
        lifecycleScope.launch {
            val user = database.userDao().getLoggedInUser().first()
            user?.let {
                displayUserInfo(it.email, it.name, it.photoUrl ?: "drawable://ic_default_user")
            } ?: run {
                // No logged in user found, go back to login screen
                startActivity(Intent(this@DashboardActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun displayUserInfo(userEmail: String, userName: String, userPhotoUrl: String) {
        // Display user information
        findViewById<TextView>(R.id.userEmail).text = userEmail
        findViewById<TextView>(R.id.userName).text = userName

        // Load user photo
        val userPhoto = findViewById<ImageView>(R.id.userPhoto)
        if (userPhotoUrl.startsWith("drawable://")) {
            // Load from drawable resources
            val resourceName = userPhotoUrl.substring("drawable://".length)
            val resourceId = resources.getIdentifier(resourceName, "drawable", packageName)
            if (resourceId != 0) {
                userPhoto.setImageResource(resourceId)
            }
        } else if (userPhotoUrl.isNotEmpty()) {
            // Load from URL
            Glide.with(this)
                .load(userPhotoUrl)
                .circleCrop()
                .into(userPhoto)
        }
    }

    private fun loadTopics() {
        val topics = listOf(
            AndroidTopic(
                "Activity Lifecycle",
                "Learn about the lifecycle of Android Activities",
                android.R.drawable.ic_menu_manage
            ),
            AndroidTopic(
                "Fragment Lifecycle",
                "Understand Fragment lifecycle and management",
                android.R.drawable.ic_menu_edit
            ),
            AndroidTopic(
                "ViewModels",
                "Master Android ViewModels and LiveData",
                android.R.drawable.ic_menu_view
            ),
            AndroidTopic(
                "Navigation",
                "Implement navigation in your Android app",
                android.R.drawable.ic_menu_compass
            ),
            AndroidTopic(
                "Room Database",
                "Work with local database using Room",
                android.R.drawable.ic_menu_save
            )
        )
        topicsAdapter.updateItems(topics.toMutableList())
    }
} 
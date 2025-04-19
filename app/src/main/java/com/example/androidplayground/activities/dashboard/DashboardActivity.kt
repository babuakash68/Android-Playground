package com.example.androidplayground.activities.dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidplayground.R
import com.example.androidplayground.activities.learning.TheoryActivity
import com.example.androidplayground.activities.learning.interactive.views.ViewsInteractiveActivity
import com.example.androidplayground.data.AppDatabase
import com.example.androidplayground.data.User
import com.example.androidplayground.data.model.AndroidTopic
import com.example.androidplayground.ui.adapter.AndroidTopicAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var topicsAdapter: AndroidTopicAdapter
    private lateinit var userIcon: ImageView
    private lateinit var signInButton: MaterialButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private var currentUser: User? = null
    private var isGuestMode: Boolean = false

    companion object {
        private const val RC_SIGN_IN = 9001
        const val EXTRA_GUEST_MODE = "guest_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        database = AppDatabase.getDatabase(this)

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        userIcon = findViewById(R.id.userIcon)
        signInButton = findViewById(R.id.signInButton)

        // Check if we're in guest mode
        isGuestMode = intent.getBooleanExtra(EXTRA_GUEST_MODE, false)
        
        if (isGuestMode) {
            // In guest mode, just show the sign-in button and hide the user icon
            showSignInButton()
        } else {
            // Make sure user icon is visible initially
            userIcon.visibility = View.VISIBLE

            // Get user from intent or database
            try {
                // Try to get user from intent first
                currentUser = intent.getParcelableExtra<User>("user")
                
                // If not in intent, try to get from database
                if (currentUser == null) {
                    lifecycleScope.launch {
                        currentUser = database.userDao().getLoggedInUser().first()
                        updateUI(currentUser)
                    }
                } else {
                    updateUI(currentUser)
                }
            } catch (e: Exception) {
                // Error occurred, show sign in button
                showSignInButton()
            }
        }

        userIcon.setOnClickListener {
            if (currentUser != null) {
                showUserMenu(it)
            } else {
                startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
            }
        }

        signInButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.topicsRecyclerView)
        topicsAdapter = AndroidTopicAdapter(this) { topic ->
            // Handle topic click
            handleTopicClick(topic)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = topicsAdapter
        }

        // Load topics
        loadTopics()
    }

    private fun showUserMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.user_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_user_info -> {
                    showUserInfoDialog()
                    true
                }
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun showUserInfoDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_user_info)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        // Add animation to the dialog
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        val userImage = dialog.findViewById<ImageView>(R.id.dialogUserImage)
        val userName = dialog.findViewById<TextView>(R.id.dialogUserName)
        val userEmail = dialog.findViewById<TextView>(R.id.dialogUserEmail)
        val logoutButton = dialog.findViewById<MaterialButton>(R.id.dialogLogoutButton)

        currentUser?.let { user ->
            userName.text = user.name
            userEmail.text = user.email
            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(userImage)
        }

        logoutButton.setOnClickListener {
            // Sign out from Google
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()

            // Update user login status in database
            currentUser?.let { user ->
                // Just update the login status in the database
                lifecycleScope.launch {
                    database.userDao().updateLoginStatus(user.email, false)
                }
            }

            // Dismiss dialog and navigate to MainActivity
            dialog.dismiss()
            logout()
            finish()
        }
        dialog.show()
    }

    private fun logout() {
        lifecycleScope.launch {
            // Update user login status
            currentUser?.let {
                database.userDao().updateLoginStatus(it.email, false)
            }
            // Sign out from Google
            googleSignInClient.signOut()
            // Show sign in button
            showSignInButton()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                
                // Create user from Google account
                val user = User(
                    email = account.email ?: "",
                    name = account.displayName ?: "",
                    photoUrl = account.photoUrl?.toString(),
                    isLoggedIn = true,
                    lastLoginTime = System.currentTimeMillis()
                )

                // Save user to database
                lifecycleScope.launch {
                    database.userDao().insertUser(user)
                    currentUser = user
                    updateUI(user)
                }
            } catch (e: ApiException) {
                // Handle sign in failure
                showSignInButton()
            }
        }
    }

    private fun loadTopics() {
        val topics = listOf(
            AndroidTopic(
                title = "Activity Lifecycle",
                description = "Learn about the Android Activity lifecycle and its states",
                iconResId = R.drawable.ic_activity_lifecycle
            ),
            AndroidTopic(
                title = "Fragment Lifecycle",
                description = "Master the power of reusable UI components in Android",
                iconResId = R.drawable.ic_fragment_lifecycle
            ),
            AndroidTopic(
                title = "Context",
                description = "Understand the fundamental concept of Context in Android",
                iconResId = R.drawable.ic_context
            ),
            AndroidTopic(
                title = "Four Pillars of Android",
                description = "Learn about the four fundamental components of Android",
                iconResId = R.drawable.ic_pillars
            ),
            AndroidTopic(
                title = "Intents",
                description = "Explore the power of Intents in Android communication",
                iconResId = R.drawable.ic_intent
            ),
            AndroidTopic(
                title = "Views and Layouts",
                description = "Master Android UI components and WebView integration",
                iconResId = R.drawable.ic_views
            )
        )
        topicsAdapter.updateItems(topics.toMutableList())
    }

    private fun handleTopicClick(topic: AndroidTopic) {
        val intent = Intent(this, TheoryActivity::class.java).apply {
            putExtra(TheoryActivity.EXTRA_TOPIC_TITLE, topic.title)
            
            when (topic.title) {
                "Activity Lifecycle" -> {
                    putExtra(TheoryActivity.EXTRA_HTML_RESOURCE, R.raw.activity_lifecycle_theory)
                    putExtra(TheoryActivity.EXTRA_INTERACTIVE_ACTIVITY, "com.example.androidplayground.activities.learning.interactive.LifecycleInteractiveActivity")
                    putExtra(TheoryActivity.EXTRA_SHOW_DIAGRAM, true)
                }
                "Fragment Lifecycle" -> {
                    putExtra(TheoryActivity.EXTRA_HTML_RESOURCE, R.raw.fragment_lifecycle_theory)
                    putExtra(TheoryActivity.EXTRA_INTERACTIVE_ACTIVITY, "com.example.androidplayground.activities.learning.interactive.fragment.FragmentInteractiveActivity")
                    putExtra(TheoryActivity.EXTRA_SHOW_DIAGRAM, false)
                }
                "Context" -> {
                    putExtra(TheoryActivity.EXTRA_HTML_RESOURCE, R.raw.context_theory)
                    putExtra(TheoryActivity.EXTRA_INTERACTIVE_ACTIVITY, "com.example.androidplayground.activities.learning.interactive.context.ContextInteractiveActivity")
                    putExtra(TheoryActivity.EXTRA_SHOW_DIAGRAM, false)
                }
                "Four Pillars of Android" -> {
                    putExtra(TheoryActivity.EXTRA_HTML_RESOURCE, R.raw.pillars_theory)
                    putExtra(TheoryActivity.EXTRA_INTERACTIVE_ACTIVITY, "com.example.androidplayground.activities.learning.pillars.PillarsInteractiveActivity")
                    putExtra(TheoryActivity.EXTRA_SHOW_DIAGRAM, false)
                }
                "Intents" -> {
                    putExtra(TheoryActivity.EXTRA_HTML_RESOURCE, R.raw.intent_theory)
                    putExtra(TheoryActivity.EXTRA_INTERACTIVE_ACTIVITY, "com.example.androidplayground.activities.learning.interactive.intent.IntentInteractiveActivity")
                    putExtra(TheoryActivity.EXTRA_SHOW_DIAGRAM, false)
                }
                "Views and Layouts" -> {
                    putExtra(TheoryActivity.EXTRA_HTML_RESOURCE, R.raw.views_layouts_theory)
                    putExtra(TheoryActivity.EXTRA_INTERACTIVE_ACTIVITY, "com.example.androidplayground.activities.learning.interactive.views.ViewsInteractiveActivity")
                    putExtra(TheoryActivity.EXTRA_SHOW_DIAGRAM, false)
                }
            }
        }
        startActivity(intent)
    }

    private fun updateUI(user: User?) {
        if (user != null && !isGuestMode) {
            // Show user icon
            userIcon.visibility = View.VISIBLE
            signInButton.visibility = View.GONE

            // Load user image
            if (user.photoUrl != null) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .circleCrop()
                    .into(userIcon)
            } else {
                userIcon.setImageResource(R.drawable.ic_profile_placeholder)
            }
            
            // Log for debugging
            android.util.Log.d("DashboardActivity", "User icon should be visible now")
        } else {
            // Show sign in button
            showSignInButton()
        }
    }

    private fun showSignInButton() {
        userIcon.visibility = View.GONE
        signInButton.visibility = View.VISIBLE
        
        // Log for debugging
        android.util.Log.d("DashboardActivity", "Sign in button should be visible now")
    }
} 
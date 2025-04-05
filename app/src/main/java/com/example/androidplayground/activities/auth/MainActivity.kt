package com.example.androidplayground.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.androidplayground.R
import com.example.androidplayground.activities.dashboard.DashboardActivity
import com.example.androidplayground.data.AppDatabase
import com.example.androidplayground.data.User
import com.example.androidplayground.databinding.ActivityMainBinding
import com.example.androidplayground.ui.auth.AuthState
import com.example.androidplayground.ui.auth.AuthViewModel
import com.example.androidplayground.ui.auth.AuthViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var viewModel: AuthViewModel

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let { viewModel.signInWithGoogle(it) }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed", e)
            Toast.makeText(this, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val database = AppDatabase.getDatabase(this)
        viewModel = ViewModelProvider(this, AuthViewModelFactory(database))[AuthViewModel::class.java]

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up click listener
        binding.signInButton.setOnClickListener {
            signInLauncher.launch(googleSignInClient.signInIntent)
        }

        // Observe auth state
        lifecycleScope.launch {
            viewModel.authState.collectLatest { state ->
                when (state) {
                    is AuthState.Initial -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signInButton.visibility = View.VISIBLE
                    }
                    is AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.signInButton.visibility = View.GONE
                    }
                    is AuthState.LoggedIn -> {
                        navigateToDashboard(state.user)
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signInButton.visibility = View.VISIBLE
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun navigateToDashboard(user: User) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("user_email", user.email)
        intent.putExtra("user_name", user.name)
        intent.putExtra("user_photo_url", user.photoUrl ?: "drawable://ic_default_user")
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
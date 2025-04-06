package com.example.androidplayground.activities.learning.intent

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityIntentInteractiveBinding
import com.example.androidplayground.databinding.DialogIntentWelcomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class IntentInteractiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntentInteractiveBinding
    private val TAG = "IntentInteractiveActivity"
    private var logEntries = mutableListOf<String>()
    
    // Achievement tracking
    private val achievements = mutableSetOf<String>()
    private val totalAchievements = 9 // Total number of possible achievements
    
    // Lifecycle tracking
    private var currentLifecycleState = "idle"

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data?.getStringExtra("result_data")
            updateResultStatus(R.string.intent_result_success, data)
            addLogEntry(getString(R.string.intent_log_explicit_result, data ?: ""))
            
            // Track explicit intent achievement
            trackAchievement("explicit")
            
            // Update intent status
            updateIntentStatus(R.string.intent_status_received)
        } else {
            updateResultStatus(R.string.intent_result_cancelled, null)
            addLogEntry(getString(R.string.intent_log_explicit_cancelled))
            
            // Update intent status
            updateIntentStatus(R.string.intent_status_idle)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentInteractiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        showWelcomeDialog()
        
        // Initialize all status indicators with proper color coding
        updateResultStatus(R.string.intent_status_idle, null)
        updateIntentStatus(R.string.intent_status_idle)
        
        // Initialize achievements UI
        updateAchievementsUI()
        
        // Update lifecycle status
        updateLifecycleStatus("created")
    }
    
    override fun onStart() {
        super.onStart()
        updateLifecycleStatus("started")
    }
    
    override fun onResume() {
        super.onResume()
        updateLifecycleStatus("active")
    }
    
    override fun onPause() {
        super.onPause()
        updateLifecycleStatus("paused")
    }
    
    override fun onStop() {
        super.onStop()
        updateLifecycleStatus("stopped")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        updateLifecycleStatus("destroyed")
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.intent_interactive_title)
    }

    private fun setupClickListeners() {
        binding.btnStartResultActivity.setOnClickListener {
            startResultActivity()
        }

        binding.btnShareContent.setOnClickListener {
            shareContent()
        }

        binding.btnOpenUrl.setOnClickListener {
            openUrl()
        }

        binding.btnDialPhone.setOnClickListener {
            dialPhone()
        }

        binding.btnSendData.setOnClickListener {
            sendDataWithIntent()
        }

        binding.btnBreakIntent.setOnClickListener {
            breakIntent()
        }

        binding.btnRevisitFlow.setOnClickListener {
            revisitFlow()
        }

        binding.btnResetLog.setOnClickListener {
            binding.tvLog.text = ""
        }

        binding.btnCopyLog.setOnClickListener {
            copyLogToClipboard()
        }

        binding.btnTheory.setOnClickListener {
            startActivity(Intent(this, IntentTheoryActivity::class.java))
        }
    }

    private fun startResultActivity() {
        val intent = Intent(this, IntentResultActivity::class.java)
        startForResult.launch(intent)
        addLogEntry(getString(R.string.intent_log_explicit_start))
        
        // Track explicit intent achievement
        trackAchievement("explicit")
        
        // Update intent status
        updateIntentStatus(R.string.intent_status_sent)
    }

    private fun shareContent() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.intent_share_text))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.intent_share_title)))
        addLogEntry(getString(R.string.intent_log_implicit_share))
        
        // Track implicit intent achievement
        trackAchievement("implicit")
        
        // Update intent status
        updateIntentStatus(R.string.intent_status_sent)
    }

    private fun openUrl() {
        val url = getString(R.string.intent_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
            addLogEntry(getString(R.string.intent_log_implicit_url))
            
            // Track implicit intent achievement
            trackAchievement("implicit")
            
            // Update intent status
            updateIntentStatus(R.string.intent_status_sent)
        } catch (e: Exception) {
            showError(getString(R.string.intent_error_no_browser))
        }
    }

    private fun dialPhone() {
        val phone = getString(R.string.intent_phone)
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        try {
            startActivity(intent)
            addLogEntry(getString(R.string.intent_log_implicit_phone))
            
            // Track implicit intent achievement
            trackAchievement("implicit")
            
            // Update intent status
            updateIntentStatus(R.string.intent_status_sent)
        } catch (e: Exception) {
            showError(getString(R.string.intent_error_no_dialer))
        }
    }

    private fun sendDataWithIntent() {
        val dataToSend = binding.etDataToSend.text.toString()
        if (dataToSend.isNotEmpty()) {
            val intent = Intent(this, IntentResultActivity::class.java).apply {
                putExtra("data_to_process", dataToSend)
            }
            startForResult.launch(intent)
            addLogEntry(getString(R.string.intent_log_data_sent, dataToSend))
            
            // Track data passing achievement
            trackAchievement("data")
            
            // Update intent status
            updateIntentStatus(R.string.intent_status_sent)
        } else {
            showError(getString(R.string.intent_error_empty_data))
        }
    }

    private fun updateResultStatus(statusResId: Int, data: String?) {
        binding.tvResultStatus.text = getString(statusResId)
        binding.tvResultData.text = data ?: getString(R.string.intent_result_no_data)
        
        // Update text color based on status
        val colorResId = when (statusResId) {
            R.string.intent_status_idle -> R.color.intent_status_idle
            R.string.intent_result_success -> R.color.intent_status_received
            R.string.intent_result_cancelled -> R.color.intent_status_idle
            else -> R.color.intent_status_idle
        }
        
        binding.tvResultStatus.setTextColor(getColor(colorResId))
    }
    
    private fun updateIntentStatus(statusResId: Int) {
        binding.tvIntentStatus.text = getString(statusResId)
        
        // Update text color based on status
        val colorResId = when (statusResId) {
            R.string.intent_status_idle -> R.color.intent_status_idle
            R.string.intent_status_sent -> R.color.intent_status_sent
            R.string.intent_status_received -> R.color.intent_status_received
            else -> R.color.intent_status_idle
        }
        
        binding.tvIntentStatus.setTextColor(getColor(colorResId))
    }
    
    private fun updateLifecycleStatus(state: String) {
        currentLifecycleState = state
        val statusResId = when (state) {
            "created" -> R.string.intent_lifecycle_idle
            "started" -> R.string.intent_lifecycle_idle
            "active" -> R.string.intent_lifecycle_active
            "paused" -> R.string.intent_lifecycle_paused
            "stopped" -> R.string.intent_lifecycle_stopped
            "destroyed" -> R.string.intent_lifecycle_destroyed
            else -> R.string.intent_lifecycle_idle
        }
        
        binding.tvLifecycleStatus.text = getString(statusResId)
        
        // Update text color based on lifecycle state
        val colorResId = when (state) {
            "active" -> R.color.intent_status_received  // Green for active
            "paused" -> R.color.intent_status_sent      // Blue for paused
            "stopped" -> R.color.intent_status_idle     // Gray for stopped
            "destroyed" -> R.color.intent_error         // Red for destroyed
            else -> R.color.intent_status_idle          // Gray for other states
        }
        
        binding.tvLifecycleStatus.setTextColor(getColor(colorResId))
        
        // Log lifecycle change
        addLogEntry(getString(R.string.intent_log_lifecycle, 
            state.capitalize(), 
            if (state == "active") "Resumed" else state.capitalize()))
    }

    private fun addLogEntry(message: String) {
        val timestamp = System.currentTimeMillis()
        val logEntry = "[$timestamp] $message\n"
        binding.tvLog.append(logEntry)
    }

    private fun copyLogToClipboard() {
        val logText = binding.tvLog.text.toString()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Intent Log", logText)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(binding.root, R.string.intent_log_copied, Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun breakIntent() {
        // Create an invalid intent to demonstrate error handling
        val invalidIntent = Intent("com.example.androidplayground.INVALID_ACTION")
        try {
            startActivity(invalidIntent)
        } catch (e: Exception) {
            // Log the error
            addLogEntry("Error: Intent resolution failed - ${e.message}")
            
            // Track error handling achievement
            trackAchievement("error")
            
            // Show a more educational error dialog
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.intent_error_title)
                .setMessage("This is an example of Intent resolution failure. The system couldn't find an activity to handle the Intent with action 'com.example.androidplayground.INVALID_ACTION'.\n\n" +
                        "In real apps, you should:\n" +
                        "1. Check if an app can handle your Intent before starting it\n" +
                        "2. Provide fallback behavior when no app is available\n" +
                        "3. Use try-catch blocks to handle exceptions gracefully")
                .setPositiveButton("Got it") { dialog, _ -> dialog.dismiss() }
                .show()
            
            // Also show a snackbar for immediate feedback
            showError("Intent resolution failed: ${e.message}")
        }
    }

    private fun revisitFlow() {
        // Reset the activity state to demonstrate the flow again
        updateResultStatus(R.string.intent_status_idle, null)
        updateIntentStatus(R.string.intent_status_idle)
        binding.etDataToSend.text?.clear()
        addLogEntry("Flow reset - Ready to start over")
        
        // Track lifecycle achievement
        trackAchievement("lifecycle")
        
        // Show a dialog explaining the purpose of this button
        MaterialAlertDialogBuilder(this)
            .setTitle("Revisit Flow")
            .setMessage("This button resets the activity state so you can try the Intent flow again from the beginning.\n\n" +
                    "It's useful for:\n" +
                    "1. Practicing the Intent flow multiple times\n" +
                    "2. Starting fresh after completing a flow\n" +
                    "3. Demonstrating the Intent lifecycle to others")
            .setPositiveButton("Got it") { dialog, _ -> dialog.dismiss() }
            .show()
        
        // Also show a snackbar for immediate feedback
        Snackbar.make(binding.root, "Flow reset. Try the activities again!", Snackbar.LENGTH_LONG).show()
    }

    private fun showWelcomeDialog() {
        val prefs = getSharedPreferences("intent_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("show_welcome", true)) {
            val dialogBinding = DialogIntentWelcomeBinding.inflate(layoutInflater)
            val dialog = MaterialAlertDialogBuilder(this)
                .setView(dialogBinding.root)
                .setCancelable(false)
                .create()

            dialogBinding.btnGotIt.setOnClickListener {
                if (dialogBinding.cbDontShowAgain.isChecked) {
                    prefs.edit().putBoolean("show_welcome", false).apply()
                }
                dialog.dismiss()
            }

            dialogBinding.btnHelp.setOnClickListener {
                // Show detailed help dialog
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.intent_welcome_help)
                    .setMessage(R.string.intent_welcome_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }

            dialog.show()
        }
    }

    // Track achievements and update UI
    private fun trackAchievement(achievementType: String) {
        if (achievements.add(achievementType)) {
            // New achievement unlocked
            updateAchievementsUI()
            
            // Check if all achievements are unlocked
            if (achievements.size == totalAchievements) {
                showAchievementCompleteDialog()
            }
        }
    }
    
    // Update the achievements UI
    private fun updateAchievementsUI() {
        binding.tvAchievements.text = getString(
            R.string.intent_achievements_progress, 
            achievements.size, 
            totalAchievements
        )
    }
    
    // Show dialog when all achievements are unlocked
    private fun showAchievementCompleteDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.intent_achievement_complete)
            .setMessage("Congratulations! You've unlocked all Intent achievements and are now an Intent Wizard!")
            .setPositiveButton("Awesome!") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
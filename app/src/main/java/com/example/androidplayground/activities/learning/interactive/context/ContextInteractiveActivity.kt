package com.example.androidplayground.activities.learning.interactive.context

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityContextInteractiveBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class ContextInteractiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContextInteractiveBinding
    private var isActivityDestroyed = false
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()

    companion object {
        private const val TAG = "ContextInteractive"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContextInteractiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupButtons()
        updateContextStatus()
        addLogEntry("Activity created", true)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupButtons() {
        // Activity Context buttons
        binding.btnShowToastActivity.setOnClickListener {
            showToastWithContext(this, "Toast from Activity Context")
        }

        binding.btnLaunchNewScreen.setOnClickListener {
            addLogEntry("Launching new screen with Activity Context", true)
            // Launch a new activity to demonstrate Activity Context
            startActivity(android.content.Intent(this, ContextInteractiveActivity::class.java))
        }

        binding.btnAccessResourceActivity.setOnClickListener {
            try {
                val resourceString = getString(R.string.app_name)
                addLogEntry("Accessed resource with Activity Context: $resourceString", true)
            } catch (e: Exception) {
                addLogEntry("Failed to access resource with Activity Context", false)
            }
        }

        // Application Context buttons
        binding.btnShowToastApp.setOnClickListener {
            showToastWithContext(applicationContext, "Toast from Application Context")
        }

        binding.btnAccessResourceApp.setOnClickListener {
            try {
                val resourceString = applicationContext.getString(R.string.app_name)
                addLogEntry("Accessed resource with Application Context: $resourceString", true)
            } catch (e: Exception) {
                addLogEntry("Failed to access resource with Application Context", false)
            }
        }

        // Control buttons
        binding.btnSimulateDestroy.setOnClickListener {
            isActivityDestroyed = true
            addLogEntry("Activity destroyed simulation", false)
            updateContextStatus()
            Snackbar.make(binding.root, "Activity Context is now invalid!", Snackbar.LENGTH_LONG).show()
        }

        binding.btnReset.setOnClickListener {
            resetDemo()
        }

        binding.btnCopyLog.setOnClickListener {
            copyLogToClipboard()
        }
    }

    private fun showToastWithContext(context: Context, message: String) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            val contextType = if (context === this) "Activity" else "Application"
            addLogEntry("Toast shown with $contextType Context: $message", true)
        } catch (e: Exception) {
            addLogEntry("Failed to show toast: ${e.message}", false)
        }
    }

    private fun addLogEntry(message: String, isSuccess: Boolean) {
        val timestamp = dateFormat.format(Date())
        val color = if (isSuccess) "#4CAF50" else "#F44336"
        logBuilder.append("<font color='$color'>[$timestamp] $message</font><br>")
        binding.tvLog.text = android.text.Html.fromHtml(logBuilder.toString(), android.text.Html.FROM_HTML_MODE_COMPACT)
    }

    private fun updateContextStatus() {
        binding.activityContextStatus.text = "Activity Context: ${if (isActivityDestroyed) "Invalid" else "Active"}"
        binding.activityContextStatus.setTextColor(
            resources.getColor(
                if (isActivityDestroyed) R.color.error else R.color.success,
                theme
            )
        )
        binding.applicationContextStatus.text = "Application Context: Always Active"
        binding.applicationContextStatus.setTextColor(resources.getColor(R.color.success, theme))
    }

    private fun resetDemo() {
        isActivityDestroyed = false
        logBuilder.clear()
        binding.tvLog.text = ""
        updateContextStatus()
        addLogEntry("Demo reset", true)
    }

    private fun copyLogToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Context Log", binding.tvLog.text)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(binding.root, "Log copied to clipboard", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity destroyed")
    }
} 
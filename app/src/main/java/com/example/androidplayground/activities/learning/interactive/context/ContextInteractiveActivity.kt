package com.example.androidplayground.activities.learning.interactive.context

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
            startActivity(Intent(this, ContextInteractiveActivity::class.java))
        }

        binding.btnAccessResourceActivity.setOnClickListener {
            try {
                // Demonstrate accessing resources with Activity Context
                val appName = getString(R.string.app_name)
                val screenWidth = resources.displayMetrics.widthPixels
                val screenHeight = resources.displayMetrics.heightPixels
                
                // Show a dialog with resource information
                showResourceInfoDialog(
                    "Activity Context Resources",
                    """
                    App Name: $appName
                    Screen Width: $screenWidth px
                    Screen Height: $screenHeight px
                    
                    Activity Context can access:
                    - Resources (strings, colors, dimensions)
                    - System services
                    - UI components
                    - Theme attributes
                    """.trimIndent()
                )
                
                addLogEntry("Accessed resources with Activity Context: $appName", true)
            } catch (e: Exception) {
                addLogEntry("Failed to access resources with Activity Context: ${e.message}", false)
            }
        }

        // Application Context buttons
        binding.btnShowToastApp.setOnClickListener {
            showToastWithContext(applicationContext, "Toast from Application Context")
        }

        binding.btnLaunchNewScreenApp.setOnClickListener {
            try {
                addLogEntry("Launching new screen with Application Context", true)
                // Launch a new activity using Application Context
                val intent = Intent(applicationContext, ContextInteractiveActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required when using Application Context
                applicationContext.startActivity(intent)
                
                showResourceInfoDialog(
                    "Application Context Activity Launch",
                    """
                    When launching activities with Application Context:
                    
                    1. You MUST add FLAG_ACTIVITY_NEW_TASK flag
                    2. The activity will start in a new task
                    3. No parent-child relationship is established
                    4. The back stack behavior may be different
                    
                    This is because Application Context doesn't have UI context.
                    """.trimIndent()
                )
            } catch (e: Exception) {
                addLogEntry("Failed to launch screen with Application Context: ${e.message}", false)
            }
        }

        binding.btnAccessResourceApp.setOnClickListener {
            try {
                // Demonstrate accessing resources with Application Context
                val appName = applicationContext.getString(R.string.app_name)
                
                // Show a dialog with resource information
                showResourceInfoDialog(
                    "Application Context Resources",
                    """
                    App Name: $appName
                    
                    Application Context can access:
                    - Resources (strings, colors, dimensions)
                    - System services
                    - Package information
                    
                    But CANNOT access:
                    - UI components
                    - Theme attributes
                    - Activity-specific features
                    """.trimIndent()
                )
                
                addLogEntry("Accessed resources with Application Context: $appName", true)
            } catch (e: Exception) {
                addLogEntry("Failed to access resources with Application Context: ${e.message}", false)
            }
        }

        // Control buttons
        binding.btnSimulateDestroy.setOnClickListener {
            isActivityDestroyed = true
            addLogEntry("Activity destroyed simulation", false)
            updateContextStatus()
            
            // Disable Activity Context buttons to show they're no longer usable
            binding.btnShowToastActivity.isEnabled = false
            binding.btnLaunchNewScreen.isEnabled = false
            binding.btnAccessResourceActivity.isEnabled = false
            
            // Show a dialog explaining what happened
            showResourceInfoDialog(
                "Activity Context Invalidated",
                """
                The Activity Context has been simulated as destroyed.
                
                What this means:
                1. The Activity Context buttons are now disabled
                2. You cannot use the Activity Context for UI operations
                3. The Application Context buttons still work
                
                In a real scenario, this would happen when:
                - The activity is finished
                - The activity is destroyed by the system
                - The activity is recreated due to configuration changes
                
                The Application Context remains valid throughout the app's lifecycle.
                """.trimIndent()
            )
            
            Snackbar.make(binding.root, "Activity Context is now invalid!", Snackbar.LENGTH_LONG).show()
        }

        binding.btnReset.setOnClickListener {
            resetDemo()
        }

        binding.btnCopyLog.setOnClickListener {
            copyLogToClipboard()
        }
    }

    private fun showResourceInfoDialog(title: String, message: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
        
        dialog.show()
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
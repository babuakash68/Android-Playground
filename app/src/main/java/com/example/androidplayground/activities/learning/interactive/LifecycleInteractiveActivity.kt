package com.example.androidplayground.activities.learning.interactive

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidplayground.R
import com.example.androidplayground.activities.learning.interactive.dummy.DummyActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class LifecycleInteractiveActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var welcomeCard: MaterialCardView
    private lateinit var welcomeMessage: TextView
    private lateinit var hintText: TextView
    private lateinit var achievementCard: MaterialCardView
    private lateinit var achievementText: TextView
    private lateinit var achievementBadge: ImageView
    private lateinit var logText: TextView
    private lateinit var theoryFootnote: TextView
    private lateinit var copyLogButton: MaterialButton

    // Interactive buttons
    private lateinit var startNewScreenButton: MaterialButton
    private lateinit var closeScreenButton: MaterialButton
    private lateinit var rotateViewButton: MaterialButton
    private lateinit var minimizeButton: MaterialButton
    private lateinit var resetButton: MaterialButton

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val triedActions = mutableSetOf<String>()
    private var isFirstInteraction = true
    private var savedLogText = ""
    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_interactive)

        // Initialize views
        initializeViews()
        setupToolbar()
        setupButtons()
        
        // Restore log text if it was saved during rotation
        if (savedInstanceState != null && savedInstanceState.containsKey("log_text")) {
            savedLogText = savedInstanceState.getString("log_text", "") ?: ""
            logText.text = android.text.Html.fromHtml(savedLogText, android.text.Html.FROM_HTML_MODE_COMPACT)
            logBuilder.append(savedLogText)
        }
        
        logLifecycleEvent("onCreate", "Setting up the interactive screen!", Color.GREEN)
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        welcomeCard = findViewById(R.id.welcomeCard)
        welcomeMessage = findViewById(R.id.welcomeMessage)
        hintText = findViewById(R.id.hintText)
        achievementCard = findViewById(R.id.achievementCard)
        achievementText = findViewById(R.id.achievementText)
        achievementBadge = findViewById(R.id.achievementBadge)
        logText = findViewById(R.id.logText)
        theoryFootnote = findViewById(R.id.theoryFootnote)
        copyLogButton = findViewById(R.id.copyLogButton)

        startNewScreenButton = findViewById(R.id.startNewScreenButton)
        closeScreenButton = findViewById(R.id.closeScreenButton)
        rotateViewButton = findViewById(R.id.rotateViewButton)
        minimizeButton = findViewById(R.id.minimizeButton)
        resetButton = findViewById(R.id.resetButton)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("topic_title") ?: "Lifecycle Interactive"
    }

    private fun setupButtons() {
        startNewScreenButton.setOnClickListener {
            if (isFirstInteraction) {
                hideWelcomeMessage()
            }
            tryAction("start_new_screen")
            logLifecycleEvent("onPause", "Starting a new screen!", Color.YELLOW)
            showTheoryRecall("When a new activity is launched, the current activity goes to the background and onPause() is called.")
            // Launch a dummy activity
            startActivity(Intent(this, DummyActivity::class.java))
        }

        closeScreenButton.setOnClickListener {
            if (isFirstInteraction) {
                hideWelcomeMessage()
            }
            tryAction("close_screen")
            logLifecycleEvent("onStop", "Closing the screen!", Color.RED)
            logLifecycleEvent("onDestroy", "Cleaning up resources!", Color.RED)
            showTheoryRecall("When an activity is closed, onStop() is called first, followed by onDestroy() to clean up resources.")
            finish()
        }

        rotateViewButton.setOnClickListener {
            if (isFirstInteraction) {
                hideWelcomeMessage()
            }
            tryAction("rotate_view")
            
            // Save current log text before recreation
            savedLogText = logBuilder.toString()
            
            // Log the rotation event
            logLifecycleEvent("onPause", "Rotating screen!", Color.YELLOW)
            logLifecycleEvent("onStop", "Screen rotation in progress!", Color.RED)
            logLifecycleEvent("onDestroy", "Recreating activity for rotation!", Color.RED)
            
            // Show a snackbar to indicate rotation
            showSnackbar("Rotating screen...")
            
            showTheoryRecall("During configuration changes like rotation, the activity is destroyed and recreated. This triggers the full lifecycle sequence.")
            
            // Actually rotate the screen by changing orientation
            val currentOrientation = resources.configuration.orientation
            requestedOrientation = if (currentOrientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        minimizeButton.setOnClickListener {
            if (isFirstInteraction) {
                hideWelcomeMessage()
            }
            tryAction("minimize")
            logLifecycleEvent("onPause", "App going to background!", Color.YELLOW)
            logLifecycleEvent("onStop", "App in background!", Color.RED)
            showTheoryRecall("When an app goes to background, onPause() is called first, followed by onStop() if the activity is no longer visible.")
            moveTaskToBack(true)
        }

        resetButton.setOnClickListener {
            if (isFirstInteraction) {
                hideWelcomeMessage()
            }
            tryAction("reset")
            logBuilder.clear()
            logText.text = ""
            triedActions.clear()
            updateAchievement()
            showSnackbar("Log cleared! Try different actions.")
        }

        copyLogButton.setOnClickListener {
            copyLogToClipboard()
        }
    }

    private fun copyLogToClipboard() {
        // Strip HTML tags and convert to plain text
        val plainText = logBuilder.toString()
            .replace(Regex("<[^>]*>"), "") // Remove HTML tags
            .replace("&nbsp;", " ") // Replace HTML spaces
            .replace("&lt;", "<") // Replace HTML entities
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .trim() // Remove extra whitespace
        
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Lifecycle Log", plainText)
        clipboardManager.setPrimaryClip(clip)
        logLifecycleEvent("System", "Log copied to clipboard", Color.GREEN)
    }

    private fun hideWelcomeMessage() {
        isFirstInteraction = false
        welcomeCard.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                welcomeCard.visibility = View.GONE
            }
            .start()
    }

    private fun logLifecycleEvent(method: String, message: String, color: Int) {
        val timestamp = dateFormat.format(Date())
        val colorHex = String.format("#%06X", 0xFFFFFF and color)
        val htmlMessage = "<font color='$colorHex'>[$timestamp] $method: $message</font><br>"
        logBuilder.append(htmlMessage)
        logText.text = android.text.Html.fromHtml(logBuilder.toString(), android.text.Html.FROM_HTML_MODE_COMPACT)
        
        // Scroll to bottom
        val scrollView = logText.parent as android.widget.ScrollView
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun tryAction(action: String) {
        if (triedActions.add(action)) {
            updateAchievement()
            if (triedActions.size == 5) {
                showAchievementBadge()
            }
        }
    }

    private fun updateAchievement() {
        achievementText.text = "Lifecycle Explorer: ${triedActions.size}/5 actions tried"
    }

    private fun showAchievementBadge() {
        achievementBadge.visibility = View.VISIBLE
        achievementBadge.alpha = 0f
        achievementBadge.scaleX = 0f
        achievementBadge.scaleY = 0f

        ObjectAnimator.ofFloat(achievementBadge, "alpha", 1f).apply {
            duration = 500
            start()
        }

        ObjectAnimator.ofFloat(achievementBadge, "scaleX", 1f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(achievementBadge, "scaleY", 1f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        showSnackbar("Congratulations! You've tried all lifecycle actions!")
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showTheoryRecall(message: String) {
        // Create a custom Snackbar with theory recall
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        val snackbarLayout = snackbarView as com.google.android.material.snackbar.Snackbar.SnackbarLayout
        
        // Create a custom layout for the Snackbar
        val customLayout = layoutInflater.inflate(R.layout.snackbar_theory_recall, null)
        val theoryText = customLayout.findViewById<TextView>(R.id.theoryRecallText)
        theoryText.text = message
        
        // Add the custom layout to the Snackbar
        snackbarLayout.addView(customLayout, 0)
        
        // Show the Snackbar
        snackbar.show()
    }

    override fun onResume() {
        super.onResume()
        logLifecycleEvent("onResume", "Screen is active!", Color.GREEN)
        
        // Reset the requested orientation to allow for multiple rotations
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onPause() {
        super.onPause()
        logLifecycleEvent("onPause", "Screen is paused!", Color.YELLOW)
    }

    override fun onStop() {
        super.onStop()
        logLifecycleEvent("onStop", "Screen is stopped!", Color.RED)
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifecycleEvent("onDestroy", "Screen is destroyed!", Color.RED)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the log text to preserve it during rotation
        outState.putString("log_text", logBuilder.toString())
    }
} 
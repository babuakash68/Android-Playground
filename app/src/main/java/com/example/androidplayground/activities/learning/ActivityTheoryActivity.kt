package com.example.androidplayground.activities.learning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidplayground.R
import com.example.androidplayground.activities.learning.interactive.LifecycleInteractiveActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ActivityTheoryActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var lifecycleDiagram: ImageView
    private lateinit var theoryCard: MaterialCardView
    private lateinit var diagramCard: MaterialCardView
    private lateinit var theoryWebView: WebView
    private lateinit var interactiveButton: MaterialButton

    // Lifecycle state containers
    private lateinit var onCreateContainer: LinearLayout
    private lateinit var onStartContainer: LinearLayout
    private lateinit var onResumeContainer: LinearLayout
    private lateinit var onPauseContainer: LinearLayout
    private lateinit var onStopContainer: LinearLayout
    private lateinit var onDestroyContainer: LinearLayout

    // Lifecycle state labels
    private lateinit var onCreateLabel: TextView
    private lateinit var onStartLabel: TextView
    private lateinit var onResumeLabel: TextView
    private lateinit var onPauseLabel: TextView
    private lateinit var onStopLabel: TextView
    private lateinit var onDestroyLabel: TextView

    // Lifecycle state use descriptions
    private lateinit var onCreateUse: TextView
    private lateinit var onStartUse: TextView
    private lateinit var onResumeUse: TextView
    private lateinit var onPauseUse: TextView
    private lateinit var onStopUse: TextView
    private lateinit var onDestroyUse: TextView

    // States list will be initialized after views are initialized
    private lateinit var states: List<Pair<String, List<View>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theory)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.appBarLayout)
        lifecycleDiagram = findViewById(R.id.lifecycleDiagram)
        theoryCard = findViewById(R.id.theoryCard)
        diagramCard = findViewById(R.id.diagramCard)
        theoryWebView = findViewById(R.id.theoryWebView)
        interactiveButton = findViewById(R.id.interactiveDemoButton)

        // Initialize lifecycle containers
        onCreateContainer = findViewById(R.id.onCreateContainer)
        onStartContainer = findViewById(R.id.onStartContainer)
        onResumeContainer = findViewById(R.id.onResumeContainer)
        onPauseContainer = findViewById(R.id.onPauseContainer)
        onStopContainer = findViewById(R.id.onStopContainer)
        onDestroyContainer = findViewById(R.id.onDestroyContainer)

        // Initialize lifecycle labels
        onCreateLabel = findViewById(R.id.onCreateLabel)
        onStartLabel = findViewById(R.id.onStartLabel)
        onResumeLabel = findViewById(R.id.onResumeLabel)
        onPauseLabel = findViewById(R.id.onPauseLabel)
        onStopLabel = findViewById(R.id.onStopLabel)
        onDestroyLabel = findViewById(R.id.onDestroyLabel)

        // Initialize lifecycle use descriptions
        onCreateUse = findViewById(R.id.onCreateUse)
        onStartUse = findViewById(R.id.onStartUse)
        onResumeUse = findViewById(R.id.onResumeUse)
        onPauseUse = findViewById(R.id.onPauseUse)
        onStopUse = findViewById(R.id.onStopUse)
        onDestroyUse = findViewById(R.id.onDestroyUse)

        // Initialize states list after views are initialized
        states = listOf(
            "onCreate" to listOf(onCreateContainer, onCreateLabel, onCreateUse),
            "onStart" to listOf(onStartContainer, onStartLabel, onStartUse),
            "onResume" to listOf(onResumeContainer, onResumeLabel, onResumeUse),
            "onPause" to listOf(onPauseContainer, onPauseLabel, onPauseUse),
            "onStop" to listOf(onStopContainer, onStopLabel, onStopUse),
            "onDestroy" to listOf(onDestroyContainer, onDestroyLabel, onDestroyUse)
        )

        // Set up toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.activity_lifecycle)

        // Configure WebView
        theoryWebView.settings.apply {
            javaScriptEnabled = true
            defaultFontSize = 16
            setDefaultTextEncodingName("utf-8")
        }

        // Read and display HTML content from raw resource
        val htmlContent = resources.openRawResource(R.raw.activity_lifecycle_theory)
            .bufferedReader()
            .use { it.readText() }
        theoryWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)

        // Set up interactive button
        interactiveButton.setOnClickListener {
            val intent = Intent(this, LifecycleInteractiveActivity::class.java)
            intent.putExtra("topic_title", getString(R.string.activity_lifecycle))
            startActivity(intent)
        }

        // Set up animations
        setupAnimations()
        startLifecycleAnimation()
    }

    private fun setupAnimations() {
        // Theory card animation
        theoryCard.alpha = 0f
        theoryCard.translationY = 100f
        theoryCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .start()

        // Diagram card animation
        diagramCard.alpha = 0f
        diagramCard.translationY = 100f
        diagramCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(200)
            .start()
    }

    private fun startLifecycleAnimation() {
        var currentStateIndex = 0
        val highlightAnimation = AnimationUtils.loadAnimation(this, R.anim.lifecycle_state_highlight)

        // Reset all views to default state
        states.forEach { (_, views) ->
            views.forEach { view ->
                view.alpha = 0.5f
                view.scaleX = 1f
                view.scaleY = 1f
            }
        }

        // Start the animation cycle
        lifecycleDiagram.post(object : Runnable {
            override fun run() {
                // Reset previous state
                val previousState = states[(currentStateIndex - 1 + states.size) % states.size]
                previousState.second.forEach { view ->
                    view.alpha = 0.5f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }

                // Highlight current state
                val currentState = states[currentStateIndex]
                currentState.second.forEach { view ->
                    view.alpha = 1f
                    // Reduce scale factor to prevent text cutoff
                    view.scaleX = 1.05f
                    view.scaleY = 1.05f
                }

                // Animate the diagram
                lifecycleDiagram.startAnimation(highlightAnimation)

                // Move to next state
                currentStateIndex = (currentStateIndex + 1) % states.size

                // Schedule next animation
                lifecycleDiagram.postDelayed(this, 2000)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
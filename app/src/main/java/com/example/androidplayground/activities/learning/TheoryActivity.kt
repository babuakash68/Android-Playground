package com.example.androidplayground.activities.learning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityTheoryBinding
import android.widget.LinearLayout
import android.widget.TextView

class TheoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTheoryBinding
    private lateinit var states: List<Pair<String, List<View>>>

    companion object {
        const val EXTRA_TOPIC_TITLE = "topic_title"
        const val EXTRA_HTML_RESOURCE = "html_resource"
        const val EXTRA_INTERACTIVE_ACTIVITY = "interactive_activity"
        const val EXTRA_SHOW_DIAGRAM = "show_diagram"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTheoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topicTitle = intent.getStringExtra(EXTRA_TOPIC_TITLE) ?: ""
        val htmlResource = intent.getIntExtra(EXTRA_HTML_RESOURCE, 0)
        val interactiveActivityClass = intent.getStringExtra(EXTRA_INTERACTIVE_ACTIVITY)
        val showDiagram = intent.getBooleanExtra(EXTRA_SHOW_DIAGRAM, false)

        setupToolbar(topicTitle)
        setupWebView(htmlResource)
        setupInteractiveButton(topicTitle, interactiveActivityClass)
        setupDiagramVisibility(showDiagram)
        
        if (showDiagram) {
            initializeLifecycleStates()
            startLifecycleAnimation()
        }
    }

    private fun initializeLifecycleStates() {
        // Initialize states list with lifecycle containers and their labels
        states = listOf(
            "onCreate" to listOf(
                binding.onCreateContainer,
                binding.onCreateLabel,
                binding.onCreateUse
            ),
            "onStart" to listOf(
                binding.onStartContainer,
                binding.onStartLabel,
                binding.onStartUse
            ),
            "onResume" to listOf(
                binding.onResumeContainer,
                binding.onResumeLabel,
                binding.onResumeUse
            ),
            "onPause" to listOf(
                binding.onPauseContainer,
                binding.onPauseLabel,
                binding.onPauseUse
            ),
            "onStop" to listOf(
                binding.onStopContainer,
                binding.onStopLabel,
                binding.onStopUse
            ),
            "onDestroy" to listOf(
                binding.onDestroyContainer,
                binding.onDestroyLabel,
                binding.onDestroyUse
            )
        )
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
        binding.lifecycleDiagram.post(object : Runnable {
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
                    view.scaleX = 1.05f
                    view.scaleY = 1.05f
                }

                // Animate the diagram
                binding.lifecycleDiagram.startAnimation(highlightAnimation)

                // Move to next state
                currentStateIndex = (currentStateIndex + 1) % states.size

                // Schedule next animation
                binding.lifecycleDiagram.postDelayed(this, 2000)
            }
        })
    }

    private fun setupToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
    }

    private fun setupWebView(htmlResource: Int) {
        binding.theoryWebView.settings.apply {
            javaScriptEnabled = true
            defaultFontSize = 16
            setDefaultTextEncodingName("utf-8")
        }

        if (htmlResource != 0) {
            val htmlContent = resources.openRawResource(htmlResource)
                .bufferedReader()
                .use { it.readText() }
            binding.theoryWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
        }
    }

    private fun setupInteractiveButton(topicTitle: String, interactiveActivityClass: String?) {
        if (interactiveActivityClass != null) {
            binding.interactiveDemoButton.setOnClickListener {
                val intent = Intent().apply {
                    setClassName(packageName, interactiveActivityClass)
                    putExtra("topic_title", topicTitle)
                }
                startActivity(intent)
            }
        } else {
            binding.interactiveDemoButton.visibility = View.GONE
        }
    }

    private fun setupDiagramVisibility(showDiagram: Boolean) {
        if (showDiagram) {
            binding.diagramCard.visibility = View.VISIBLE
        } else {
            binding.diagramCard.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
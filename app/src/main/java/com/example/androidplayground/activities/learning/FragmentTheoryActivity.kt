package com.example.androidplayground.activities.learning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidplayground.R
import com.example.androidplayground.activities.learning.interactive.fragment.FragmentInteractiveActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class FragmentTheoryActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var theoryWebView: WebView
    private lateinit var interactiveDemoButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_theory)

        // Initialize views
        initializeViews()
        setupToolbar()
        setupWebView()
        setupInteractiveButton()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        theoryWebView = findViewById(R.id.theoryWebView)
        interactiveDemoButton = findViewById(R.id.interactiveDemoButton)

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.fragment_lifecycle)
    }

    private fun setupWebView() {
        theoryWebView.settings.apply {
            javaScriptEnabled = true
            defaultFontSize = 16
            setDefaultTextEncodingName("utf-8")
        }

        // Read and display HTML content from raw resource
        val htmlContent = resources.openRawResource(R.raw.fragment_lifecycle_theory)
            .bufferedReader()
            .use { it.readText() }
        theoryWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    }

    private fun setupInteractiveButton() {
        interactiveDemoButton.setOnClickListener {
            val intent = Intent(this, FragmentInteractiveActivity::class.java)
            intent.putExtra("topic_title", getString(R.string.fragment_lifecycle))
            startActivity(intent)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
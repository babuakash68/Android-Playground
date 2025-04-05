package com.example.androidplayground.activities.learning.theory.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.androidplayground.R
import com.example.androidplayground.activities.learning.interactive.fragment.FragmentInteractiveActivity
import com.example.androidplayground.databinding.ActivityFragmentTheoryBinding

class FragmentTheoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentTheoryBinding

    // States list will be initialized after views are initialized
    private lateinit var states: List<Pair<String, View>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentTheoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupWebView()
        setupInteractiveButton()
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.fragment_lifecycle)
    }

    private fun setupWebView() {
        binding.theoryWebView.settings.apply {
            javaScriptEnabled = true
            defaultFontSize = 16
            setDefaultTextEncodingName("utf-8")
        }

        // Read and display HTML content from raw resource
        val htmlContent = resources.openRawResource(R.raw.fragment_lifecycle_theory)
            .bufferedReader()
            .use { it.readText() }
        binding.theoryWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
    }

    private fun setupInteractiveButton() {
        binding.interactiveDemoButton.setOnClickListener {
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
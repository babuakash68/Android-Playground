package com.example.androidplayground.activities.learning.interactive.dummy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidplayground.R
import com.google.android.material.button.MaterialButton

class DummyActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var closeButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        closeButton = findViewById(R.id.closeButton)

        // Set up toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Dummy Activity"

        // Set up close button
        closeButton.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
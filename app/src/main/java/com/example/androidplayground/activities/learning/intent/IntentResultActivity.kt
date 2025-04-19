package com.example.androidplayground.activities.learning.intent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityIntentResultBinding

class IntentResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntentResultBinding
    private val TAG = "IntentResultActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        processReceivedData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.intent_result_activity_title)
    }

    private fun setupClickListeners() {
        binding.btnSuccess.setOnClickListener {
            val data = binding.etResultData.text.toString()
            val resultIntent = Intent().apply {
                putExtra("result_data", data)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun processReceivedData() {
        val receivedData = intent.getStringExtra("data_to_process")
        if (receivedData != null) {
            binding.tvReceivedData.text = getString(R.string.intent_result_activity_data, receivedData)
            binding.etResultData.setText(receivedData)
        } else {
            binding.tvReceivedData.text = getString(R.string.intent_result_activity_no_data)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
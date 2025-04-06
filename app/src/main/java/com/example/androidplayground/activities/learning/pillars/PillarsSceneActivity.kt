package com.example.androidplayground.activities.learning.pillars

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityPillarsSceneBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PillarsSceneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPillarsSceneBinding
    private val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private lateinit var instance: PillarsSceneActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = ActivityPillarsSceneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        updateLifecycleState(R.string.pillars_scene_created, "Scene Activity Created")
        
        // Log to parent activity
        PillarsInteractiveActivity.getInstance()?.logAction("Scene", "onCreate() called", true)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateLifecycleState(stateResId: Int, message: String) {
        val currentTime = timestamp.format(Date())
        val stateText = getString(stateResId)
        binding.tvLifecycleState.text = stateText
        
        // Show toast with timestamp
        Toast.makeText(this, "[$currentTime] $message", Toast.LENGTH_SHORT).show()
        
        // Log to the parent activity if possible
        PillarsInteractiveActivity.getInstance()?.logAction("Scene", message, true)
    }

    override fun onStart() {
        super.onStart()
        updateLifecycleState(R.string.pillars_scene_started, "Scene Activity Started")
        PillarsInteractiveActivity.getInstance()?.logAction("Scene", "onStart() called", true)
    }

    override fun onResume() {
        super.onResume()
        updateLifecycleState(R.string.pillars_scene_resumed, "Scene Activity Resumed")
        PillarsInteractiveActivity.getInstance()?.logAction("Scene", "onResume() called", true)
    }

    override fun onPause() {
        super.onPause()
        updateLifecycleState(R.string.pillars_scene_paused, "Scene Activity Paused")
        PillarsInteractiveActivity.getInstance()?.logAction("Scene", "onPause() called", true)
    }

    override fun onStop() {
        super.onStop()
        updateLifecycleState(R.string.pillars_scene_stopped, "Scene Activity Stopped")
        PillarsInteractiveActivity.getInstance()?.logAction("Scene", "onStop() called", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateLifecycleState(R.string.pillars_scene_destroyed, "Scene Activity Destroyed")
        PillarsInteractiveActivity.getInstance()?.logAction("Scene", "onDestroy() called", true)
    }
} 
package com.example.androidplayground.activities.learning.intent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidplayground.R
import com.example.androidplayground.adapters.IntentTopicAdapter
import com.example.androidplayground.databinding.ActivityIntentTheoryBinding
import com.example.androidplayground.models.IntentTopic
import com.google.android.material.snackbar.Snackbar

class IntentTheoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntentTheoryBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentTheoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.intent_theory_title)
    }
    
    private fun setupRecyclerView() {
        val topics = getIntentTopics()
        val adapter = IntentTopicAdapter(topics) { topic ->
            // Handle topic click - could navigate to specific topic or show more details
            Snackbar.make(binding.root, "Selected: ${topic.title}", Snackbar.LENGTH_SHORT).show()
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@IntentTheoryActivity)
            this.adapter = adapter
        }
    }
    
    private fun getIntentTopics(): List<IntentTopic> {
        return listOf(
            IntentTopic(
                id = "explicit",
                title = getString(R.string.intent_explicit_title),
                description = "Start activities and receive results",
                theory = "Explicit intents specify the exact component to start. They are used when you know the specific class name of the component you want to start. This is the most common type of intent for starting activities within your own app.",
                iconResId = R.drawable.ic_intent,
                colorResId = R.color.intent_explicit
            ),
            IntentTopic(
                id = "implicit",
                title = getString(R.string.intent_implicit_title),
                description = "Share content and open system apps",
                theory = "Implicit intents do not specify a specific component. Instead, they declare a general action to perform, which allows a component from another app to handle it. The system matches the intent to an appropriate component based on the intent filter.",
                iconResId = R.drawable.ic_intent,
                colorResId = R.color.intent_implicit
            ),
            IntentTopic(
                id = "data",
                title = "Data Passing",
                description = "Pass data between activities",
                theory = "Data passing allows you to send information from one activity to another. This is done using extras in the intent. The receiving activity can then retrieve the data using the appropriate getter method based on the data type.",
                iconResId = R.drawable.ic_intent,
                colorResId = R.color.intent_data
            ),
            IntentTopic(
                id = "result",
                title = "Activity Results",
                description = "Receive data back from activities",
                theory = "Activity results allow you to start an activity and receive data back when it finishes. This is done using the registerForActivityResult API, which provides a type-safe way to handle activity results.",
                iconResId = R.drawable.ic_intent,
                colorResId = R.color.intent_explicit
            ),
            IntentTopic(
                id = "error",
                title = "Error Handling",
                description = "Handle intent resolution failures",
                theory = "Error handling is crucial when working with intents. You should always check if an app can handle your intent before starting it, and provide fallback behavior when no app is available. Use try-catch blocks to handle exceptions gracefully.",
                iconResId = R.drawable.ic_intent,
                colorResId = R.color.intent_error
            ),
            IntentTopic(
                id = "lifecycle",
                title = "Activity Lifecycle",
                description = "Understand activity state changes",
                theory = "The activity lifecycle consists of a series of callback methods that are called as an activity moves through different states. Understanding the lifecycle is crucial for properly managing resources and ensuring a smooth user experience.",
                iconResId = R.drawable.ic_intent,
                colorResId = R.color.intent_log_lifecycle
            ),
            IntentTopic(
                id = "views_layouts",
                title = getString(R.string.intent_views_layouts_title),
                description = getString(R.string.intent_views_layouts_description),
                theory = getString(R.string.intent_views_layouts_theory),
                iconResId = R.drawable.ic_views_layouts,
                colorResId = R.color.intent_views_layouts
            )
        )
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
package com.example.androidplayground.models

import android.content.Context
import android.content.Intent

data class Topic(
    val title: String,
    val description: String,
    val iconResId: Int,
    val theoryActivityClass: Class<*>,
    val interactiveActivityClass: Class<*>
) {
    fun createTheoryIntent(context: Context): Intent {
        return Intent(context, theoryActivityClass).apply {
            putExtra("title", title)
            putExtra("description", description)
        }
    }

    fun createInteractiveIntent(context: Context): Intent {
        return Intent(context, interactiveActivityClass).apply {
            putExtra("title", title)
            putExtra("description", description)
        }
    }
} 
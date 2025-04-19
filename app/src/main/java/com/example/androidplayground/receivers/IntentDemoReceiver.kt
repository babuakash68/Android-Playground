package com.example.androidplayground.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.androidplayground.R

class IntentDemoReceiver : BroadcastReceiver() {
    private val TAG = "IntentDemoReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Broadcast received: ${intent.action}")
        
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                Log.d(TAG, "Power connected broadcast received")
                Toast.makeText(context, R.string.intent_broadcast_power_connected, Toast.LENGTH_SHORT).show()
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d(TAG, "Power disconnected broadcast received")
                Toast.makeText(context, R.string.intent_broadcast_power_disconnected, Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d(TAG, "Unknown broadcast received: ${intent.action}")
            }
        }
    }
} 
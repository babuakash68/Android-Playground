package com.example.androidplayground.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.androidplayground.R

class IntentDemoService : Service() {
    private val TAG = "IntentDemoService"
    private val CHANNEL_ID = "IntentDemoChannel"
    private val NOTIFICATION_ID = 1
    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand")
        
        if (isRunning) {
            Log.d(TAG, "Service already running")
            return START_STICKY
        }
        
        isRunning = true
        startForeground()
        startCountDown()
        
        return START_STICKY
    }

    private fun startForeground() {
        Log.d(TAG, "Starting foreground service with notification")
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.intent_service_title))
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.ic_intent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            Log.d(TAG, "Foreground service started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground service: ${e.message}")
        }
    }

    private fun createNotificationChannel() {
        Log.d(TAG, "Creating notification channel")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.intent_service_title)
            val descriptionText = "Channel for Intent Demo Service"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    private fun startCountDown() {
        Log.d(TAG, "Starting countdown timer")
        
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                Log.d(TAG, "Service running: $secondsRemaining seconds remaining")
                
                // Update notification with remaining time
                updateNotification(secondsRemaining.toInt())
            }

            override fun onFinish() {
                Log.d(TAG, "Service countdown finished")
                stopSelf()
            }
        }.start()
    }
    
    private fun updateNotification(secondsRemaining: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.intent_service_title))
            .setContentText("Service running: $secondsRemaining seconds remaining")
            .setSmallIcon(R.drawable.ic_intent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
            
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        isRunning = false
        Log.d(TAG, "Service onDestroy")
    }
} 
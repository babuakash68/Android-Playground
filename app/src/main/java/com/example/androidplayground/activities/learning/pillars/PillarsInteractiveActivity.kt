package com.example.androidplayground.activities.learning.pillars

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityPillarsInteractiveBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import android.content.ContentValues
import android.widget.EditText
import android.widget.CheckBox
import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.webkit.WebView

class PillarsInteractiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPillarsInteractiveBinding
    private var isServiceRunning = false
    private var isReceiverRegistered = false
    private var serviceStartTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimer = object : Runnable {
        override fun run() {
            if (isServiceRunning) {
                updateServiceTimer()
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    logAction("Receiver", "Power connected broadcast received", true)
                    Toast.makeText(context, "Power connected!", Toast.LENGTH_SHORT).show()
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    logAction("Receiver", "Power disconnected broadcast received", true)
                    Toast.makeText(context, "Power disconnected!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val message = intent.getStringExtra("message") ?: "Custom broadcast received"
                    logAction("Receiver", "Received broadcast: $message", true)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = ActivityPillarsInteractiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        logAction("System", "Activity created", true)
        
        // Restore service state if it was running
        if (savedInstanceState != null && savedInstanceState.getBoolean("isServiceRunning", false)) {
            isServiceRunning = true
            serviceStartTime = savedInstanceState.getLong("serviceStartTime", System.currentTimeMillis())
            binding.tvServicesStatus.text = getString(R.string.pillars_services_running)
            handler.post(updateTimer)
            logAction("Service", "Service state restored after activity recreation", true)
        }
        
        // Restore receiver state if it was registered
        if (savedInstanceState != null && savedInstanceState.getBoolean("isReceiverRegistered", false)) {
            registerReceiver()
            logAction("Receiver", "Receiver state restored after activity recreation", true)
        } else {
            // Set initial button text
            binding.btnToggleListener.text = getString(R.string.pillars_receivers_toggle_listener)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isServiceRunning", isServiceRunning)
        outState.putLong("serviceStartTime", serviceStartTime)
        outState.putBoolean("isReceiverRegistered", isReceiverRegistered)
    }

    override fun onStart() {
        super.onStart()
        logAction("System", "Activity started", true)
    }

    override fun onResume() {
        super.onResume()
        logAction("System", "Activity resumed", true)
        
        // If service is running, update the timer immediately
        if (isServiceRunning) {
            updateServiceTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        logAction("System", "Activity paused", true)
    }

    override fun onStop() {
        super.onStop()
        logAction("System", "Activity stopped", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        logAction("System", "Activity destroyed", true)
        // Only stop the service if the activity is being destroyed, not recreated
        if (isFinishing) {
            if (isServiceRunning) stopService()
            if (isReceiverRegistered) unregisterReceiver(customReceiver)
        }
        handler.removeCallbacks(updateTimer)
        instance = null
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupClickListeners() {
        // Activities
        binding.btnOpenScene.setOnClickListener {
            logAction("Activity", "Opening new scene activity", true)
            logAction("Activity", "onPause() called (current activity)", true)
            startActivity(Intent(this, PillarsSceneActivity::class.java))
            logAction("Activity", "Scene activity launched", true)
            logAction("Activity", "onResume() called (current activity)", true)
            binding.tvActivitiesStatus.text = getString(R.string.pillars_activities_active)
        }

        binding.btnFlipScreen.setOnClickListener {
            logAction("Activity", "Initiating screen rotation", true)
            logAction("Activity", "onSaveInstanceState() called", true)
            
            // Explain what's happening with the service
            if (isServiceRunning) {
                logAction("Service", "Service will continue running during activity recreation", true)
                Toast.makeText(this, "Service will continue running during activity recreation", Toast.LENGTH_LONG).show()
            }
            
            logAction("Activity", "onPause() called", true)
            logAction("Activity", "onStop() called", true)
            logAction("Activity", "onDestroy() called", true)
            
            // Show a toast to explain what's happening
            Toast.makeText(this, "Rotating screen...", Toast.LENGTH_SHORT).show()
            
            // Add a small delay to make the process visible
            Handler(Looper.getMainLooper()).postDelayed({
                // Toggle between portrait and landscape
                val currentOrientation = resources.configuration.orientation
                val newOrientation = if (currentOrientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
                    android.content.res.Configuration.ORIENTATION_LANDSCAPE
                } else {
                    android.content.res.Configuration.ORIENTATION_PORTRAIT
                }
                
                // Set the new orientation
                requestedOrientation = if (newOrientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                
                recreate()
                logAction("Activity", "onCreate() called", true)
                logAction("Activity", "onStart() called", true)
                logAction("Activity", "onResume() called", true)
                logAction("Activity", "Screen rotated - Activity recreated", true)
            }, 1000)
        }

        // Services
        binding.btnStartTask.setOnClickListener {
            startService()
        }

        binding.btnStopTask.setOnClickListener {
            stopService()
        }

        // Broadcast Receivers
        binding.btnTriggerAlert.setOnClickListener {
            // Check if receiver is registered before showing dialog
            if (!isReceiverRegistered) {
                Toast.makeText(this, "Please register a broadcast receiver first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            
            // Show an informative popup about what will happen
            android.app.AlertDialog.Builder(this)
                .setTitle("Trigger Alert")
                .setMessage("This will send a custom broadcast message. The registered receiver will also listen for power connection events.\n\n" +
                        "What you'll learn:\n" +
                        "• How broadcasts are sent in Android\n" +
                        "• How BroadcastReceivers receive and process messages\n" +
                        "• The relationship between sending and receiving broadcasts")
                .setPositiveButton("Send Broadcast") { _, _ ->
                    sendCustomBroadcast()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnToggleListener.setOnClickListener {
            // Show an informative popup about what will happen
            val action = if (isReceiverRegistered) "Unregister Broadcast" else "Register Broadcast"
            val message = if (isReceiverRegistered) 
                "This will unregister the BroadcastReceiver, stopping it from receiving any broadcasts.\n\n" +
                "What you'll learn:\n" +
                "• How to unregister BroadcastReceivers\n" +
                "• Why unregistering is important for resource management\n" +
                "• The lifecycle of BroadcastReceivers"
            else 
                "This will register a BroadcastReceiver to listen for power connection events.\n\n" +
                "What you'll learn:\n" +
                "• How to register BroadcastReceivers for power connection events\n" +
                "• How BroadcastReceivers respond to power connection events\n" +
                "• The power of event-driven programming in Android"
            
            android.app.AlertDialog.Builder(this)
                .setTitle(action)
                .setMessage(message)
                .setPositiveButton(action) { _, _ ->
                    toggleReceiver()
                    
                    // Show a toast to explain what happened
                    val toastMessage = if (isReceiverRegistered) 
                        "BroadcastReceiver registered - listening for power connection events" 
                    else 
                        "BroadcastReceiver unregistered - no longer listening for power connection events"
                    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnReceiverInfo.setOnClickListener {
            showReceiverInfo()
        }

        // Content Providers
        binding.btnAddItem.setOnClickListener {
            addItem()
        }

        binding.btnImportContacts.setOnClickListener {
            importContacts()
        }

        binding.btnViewList.setOnClickListener {
            viewList()
        }

        binding.btnEraseAll.setOnClickListener {
            eraseAll()
        }

        binding.btnProviderInfo.setOnClickListener {
            showProviderInfo()
        }

        // Log actions
        binding.btnReset.setOnClickListener {
            resetDemo()
        }

        binding.btnCopyLog.setOnClickListener {
            copyLog()
        }
    }

    private fun startService() {
        if (!isServiceRunning) {
            isServiceRunning = true
            serviceStartTime = System.currentTimeMillis()
            binding.tvServicesStatus.text = getString(R.string.pillars_services_running)
            binding.tvServiceTimer.text = "00:00"
            handler.post(updateTimer)
            logAction("Service", "Service started - will continue running even if app is closed", true)
            logAction("Service", "Service is running in the background", true)
            
            // Show a toast to explain what's happening
            Toast.makeText(this, "Service started - will continue running in background", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopService() {
        if (isServiceRunning) {
            isServiceRunning = false
            handler.removeCallbacks(updateTimer)
            binding.tvServicesStatus.text = getString(R.string.pillars_services_idle)
            binding.tvServiceTimer.text = "00:00"
            logAction("Service", "Service stopped", true)
        }
    }

    private fun updateServiceTimer() {
        val elapsedMillis = System.currentTimeMillis() - serviceStartTime
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60
        binding.tvServiceTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun toggleReceiver() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(customReceiver)
                isReceiverRegistered = false
                binding.tvReceiversStatus.text = getString(R.string.pillars_receivers_idle)
                binding.btnToggleListener.text = getString(R.string.pillars_receivers_toggle_listener)
                logAction("Receiver", "Listener unregistered", true)
                Toast.makeText(this, "BroadcastReceiver unregistered", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                logAction("Receiver", "Failed to unregister receiver: ${e.message}", false)
                Toast.makeText(this, "Failed to unregister receiver", Toast.LENGTH_SHORT).show()
            }
        } else {
            registerReceiver()
        }
    }
    
    private fun registerReceiver() {
        try {
            // Check if already registered to avoid duplicate registration
            if (!isReceiverRegistered) {
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_POWER_CONNECTED)
                    addAction(Intent.ACTION_POWER_DISCONNECTED)
                }
                registerReceiver(customReceiver, filter)
                isReceiverRegistered = true
                binding.tvReceiversStatus.text = getString(R.string.pillars_receivers_active)
                binding.btnToggleListener.text = getString(R.string.pillars_receivers_unregister_broadcast)
                logAction("Receiver", "Listener registered for power connection events", true)
                Toast.makeText(this, "BroadcastReceiver registered for power events", Toast.LENGTH_SHORT).show()
            } else {
                logAction("Receiver", "Receiver already registered", true)
            }
        } catch (e: Exception) {
            logAction("Receiver", "Failed to register receiver: ${e.message}", false)
            Toast.makeText(this, "Failed to register receiver", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCustomBroadcast() {
        // Check if receiver is registered before sending broadcast
        if (!isReceiverRegistered) {
            logAction("Receiver", "Cannot send broadcast - no listener registered", false)
            Toast.makeText(this, "Please register a broadcast receiver first", Toast.LENGTH_LONG).show()
            return
        }
        
        val intent = Intent("com.example.androidplayground.CUSTOM_ACTION").apply {
            putExtra("message", "Hello from PillarsInteractiveActivity!")
        }
        sendBroadcast(intent)
        logAction("Receiver", "Broadcast sent with action 'com.example.androidplayground.CUSTOM_ACTION'", true)
        logAction("Receiver", "Broadcast will be received by the registered listener", true)
        Toast.makeText(this, "Broadcast sent - receiver is registered", Toast.LENGTH_SHORT).show()
    }

    private fun addItem() {
        // Show an informative popup about what will happen
        android.app.AlertDialog.Builder(this)
            .setTitle("Add Contact")
            .setMessage("This will add a new contact to the Content Provider.\n\n" +
                    "What you'll learn:\n" +
                    "• How Content Providers store and manage data\n" +
                    "• How to insert data into a Content Provider\n" +
                    "• How Content Providers enable data sharing between apps")
            .setPositiveButton("Add Contact") { _, _ ->
                // Create a dialog to get contact details
                val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
                val nameEditText = dialogView.findViewById<EditText>(R.id.etContactName)
                val phoneEditText = dialogView.findViewById<EditText>(R.id.etContactPhone)
                val emailEditText = dialogView.findViewById<EditText>(R.id.etContactEmail)
                val addressEditText = dialogView.findViewById<EditText>(R.id.etContactAddress)
                val favoriteCheckBox = dialogView.findViewById<CheckBox>(R.id.cbContactFavorite)
                
                // Show the dialog to get contact details
                android.app.AlertDialog.Builder(this)
                    .setTitle("New Contact")
                    .setView(dialogView)
                    .setPositiveButton("Save") { _, _ ->
                        // Insert a new contact into the Content Provider
                        val values = ContentValues().apply {
                            put(PillarsContentProvider.COLUMN_NAME, nameEditText.text.toString())
                            put(PillarsContentProvider.COLUMN_PHONE, phoneEditText.text.toString())
                            put(PillarsContentProvider.COLUMN_EMAIL, emailEditText.text.toString())
                            put(PillarsContentProvider.COLUMN_ADDRESS, addressEditText.text.toString())
                            put(PillarsContentProvider.COLUMN_FAVORITE, favoriteCheckBox.isChecked)
                        }
                        
                        try {
                            val uri = contentResolver.insert(PillarsContentProvider.CONTENT_URI, values)
                            if (uri != null) {
                                binding.tvProvidersStatus.text = getString(R.string.pillars_provider_accessed)
                                logAction("Provider", "Contact added: ${values.getAsString(PillarsContentProvider.COLUMN_NAME)}", true)
                                Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                logAction("Provider", "Failed to add contact", false)
                                Toast.makeText(this, "Failed to add contact", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            logAction("Provider", "Error adding contact: ${e.message}", false)
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun viewList() {
        // Show an informative popup about what will happen
        android.app.AlertDialog.Builder(this)
            .setTitle("View Contacts")
            .setMessage("This will query the Content Provider to retrieve all contacts.\n\n" +
                    "What you'll learn:\n" +
                    "• How to query data from a Content Provider\n" +
                    "• How Content Providers return data as Cursors\n" +
                    "• How to process and display data from a Content Provider")
            .setPositiveButton("View Contacts") { _, _ ->
                try {
                    // Query the Content Provider
                    val cursor = contentResolver.query(
                        PillarsContentProvider.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )
                    
                    if (cursor != null && cursor.count > 0) {
                        // Create a list to hold contact details
                        val contactsList = mutableListOf<String>()
                        
                        // Process the cursor
                        cursor.moveToFirst()
                        do {
                            val id = cursor.getString(cursor.getColumnIndexOrThrow(PillarsContentProvider.COLUMN_ID))
                            val name = cursor.getString(cursor.getColumnIndexOrThrow(PillarsContentProvider.COLUMN_NAME))
                            val phone = cursor.getString(cursor.getColumnIndexOrThrow(PillarsContentProvider.COLUMN_PHONE))
                            val email = cursor.getString(cursor.getColumnIndexOrThrow(PillarsContentProvider.COLUMN_EMAIL))
                            val address = cursor.getString(cursor.getColumnIndexOrThrow(PillarsContentProvider.COLUMN_ADDRESS))
                            val favorite = cursor.getInt(cursor.getColumnIndexOrThrow(PillarsContentProvider.COLUMN_FAVORITE)) == 1
                            
                            val contactDetails = """
                                Name: $name
                                Phone: $phone
                                Email: $email
                                Address: $address
                                Favorite: ${if (favorite) "Yes" else "No"}
                                ----------------------
                            """.trimIndent()
                            
                            contactsList.add(contactDetails)
                        } while (cursor.moveToNext())
                        
                        // Show the contacts in a dialog
                        android.app.AlertDialog.Builder(this)
                            .setTitle("Contacts in Content Provider")
                            .setMessage(contactsList.joinToString("\n\n"))
                            .setPositiveButton("OK", null)
                            .show()
                        
                        logAction("Provider", "Retrieved ${cursor.count} contacts from Content Provider", true)
                    } else {
                        logAction("Provider", "No contacts found in Content Provider", false)
                        Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show()
                    }
                    
                    cursor?.close()
                } catch (e: Exception) {
                    logAction("Provider", "Error querying Content Provider: ${e.message}", false)
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun eraseAll() {
        // Show an informative popup about what will happen
        android.app.AlertDialog.Builder(this)
            .setTitle("Erase All Contacts")
            .setMessage("This will delete all contacts from the Content Provider.\n\n" +
                    "What you'll learn:\n" +
                    "• How to delete data from a Content Provider\n" +
                    "• How Content Providers manage data persistence\n" +
                    "• The importance of data management in Android apps\n\n" +
                    "Privacy Note: This will permanently delete all contacts from the Content Provider. " +
                    "Since this is a demo app, all data is stored only in memory and will be cleared when you close the app anyway.")
            .setPositiveButton("Erase All") { _, _ ->
                try {
                    // Delete all contacts from the Content Provider
                    val count = contentResolver.delete(PillarsContentProvider.CONTENT_URI, null, null)
                    
                    if (count > 0) {
                        binding.tvProvidersStatus.text = getString(R.string.pillars_provider_not_accessed)
                        logAction("Provider", "Deleted $count contacts from Content Provider", true)
                        Toast.makeText(this, "All contacts deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        logAction("Provider", "No contacts to delete", false)
                        Toast.makeText(this, "No contacts to delete", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    logAction("Provider", "Error deleting contacts: ${e.message}", false)
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetDemo() {
        // Reset all states
        if (isServiceRunning) stopService()
        if (isReceiverRegistered) toggleReceiver()
        binding.tvActivitiesStatus.text = getString(R.string.pillars_activities_idle)
        binding.tvServicesStatus.text = getString(R.string.pillars_services_idle)
        binding.tvReceiversStatus.text = getString(R.string.pillars_receivers_idle)
        binding.tvProvidersStatus.text = getString(R.string.pillars_provider_not_accessed)
        binding.tvLog.text = ""
        logAction("System", "Demo reset", true)
    }

    private fun copyLog() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Pillars Log", binding.tvLog.text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Log copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun importContacts() {
        // Show an informative popup about what will happen
        android.app.AlertDialog.Builder(this)
            .setTitle("Import Device Contacts")
            .setMessage("This will import contacts from your device's Contacts app into our Content Provider.\n\n" +
                    "What you'll learn:\n" +
                    "• How Content Providers can access data from other apps\n" +
                    "• How to request and handle permissions\n" +
                    "• How to query the system's Content Providers\n\n" +
                    "Privacy Note: This is a demo app and we are not saving or sharing your contacts data anywhere. " +
                    "All data is stored only in memory and will be cleared when you close the app. " +
                    "You can also clear all imported contacts using the 'Erase All' button before importing.")
            .setPositiveButton("Import Contacts") { _, _ ->
                // Check if there are already contacts in the provider
                val cursor = contentResolver.query(
                    PillarsContentProvider.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                
                val existingContacts = cursor?.count ?: 0
                cursor?.close()
                
                if (existingContacts > 0) {
                    // Show a confirmation dialog if there are existing contacts
                    android.app.AlertDialog.Builder(this)
                        .setTitle("Existing Contacts")
                        .setMessage("There are already $existingContacts contacts in the Content Provider. " +
                                "Would you like to clear them before importing new contacts?")
                        .setPositiveButton("Clear and Import") { _, _ ->
                            // Clear existing contacts and proceed with import
                            contentResolver.delete(PillarsContentProvider.CONTENT_URI, null, null)
                            proceedWithContactImport()
                        }
                        .setNegativeButton("Import Anyway") { _, _ ->
                            // Proceed with import without clearing
                            proceedWithContactImport()
                        }
                        .setNeutralButton("Cancel", null)
                        .show()
                } else {
                    // No existing contacts, proceed with import
                    proceedWithContactImport()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun proceedWithContactImport() {
        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) 
            != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed with import
            performContactImport()
        }
    }

    private fun performContactImport() {
        try {
            // Query the device's contacts
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
            )
            
            val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} IS NOT NULL"
            
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )
            
            if (cursor != null && cursor.count > 0) {
                var importedCount = 0
                
                // Process the cursor
                cursor.moveToFirst()
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    
                    // Create a new contact in our Content Provider
                    val values = ContentValues().apply {
                        put(PillarsContentProvider.COLUMN_NAME, name)
                        put(PillarsContentProvider.COLUMN_PHONE, phone)
                        put(PillarsContentProvider.COLUMN_EMAIL, "") // We don't have email in this query
                        put(PillarsContentProvider.COLUMN_ADDRESS, "") // We don't have address in this query
                        put(PillarsContentProvider.COLUMN_FAVORITE, false)
                    }
                    
                    try {
                        val uri = contentResolver.insert(PillarsContentProvider.CONTENT_URI, values)
                        if (uri != null) {
                            importedCount++
                        }
                    } catch (e: Exception) {
                        logAction("Provider", "Error importing contact $name: ${e.message}", false)
                    }
                } while (cursor.moveToNext())
                
                cursor.close()
                
                // Update UI and log
                binding.tvProvidersStatus.text = getString(R.string.pillars_provider_accessed)
                logAction("Provider", "Imported $importedCount contacts from device", true)
                Toast.makeText(this, "Imported $importedCount contacts from device", Toast.LENGTH_SHORT).show()
            } else {
                logAction("Provider", "No contacts found on device", false)
                Toast.makeText(this, "No contacts found on device", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            logAction("Provider", "Error importing contacts: ${e.message}", false)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            CONTACTS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with import
                    performContactImport()
                } else {
                    // Permission denied
                    logAction("Provider", "Permission to access contacts denied", false)
                    Toast.makeText(this, "Permission to access contacts denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showProviderInfo() {
        showInfoDialog(
            title = getString(R.string.pillars_providers_info),
            htmlResourceId = R.raw.provider_info
        )
    }

    private fun showReceiverInfo() {
        showInfoDialog(
            title = getString(R.string.pillars_receivers_info),
            htmlResourceId = R.raw.broadcast_receiver_info
        )
    }

    /**
     * Generic method to show an information dialog with HTML content
     * @param title The title of the dialog
     * @param htmlResourceId The resource ID of the HTML file to load
     */
    private fun showInfoDialog(title: String, htmlResourceId: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_info, null)
        val webView = dialogView.findViewById<WebView>(R.id.webView)
        
        // Enable JavaScript (optional, but might be needed for some features)
        webView.settings.javaScriptEnabled = true
        
        // Load the HTML file from raw resources
        try {
            val inputStream = resources.openRawResource(htmlResourceId)
            val htmlContent = inputStream.bufferedReader().use { it.readText() }
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading content: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        
        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    fun logAction(category: String, message: String, success: Boolean) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val color = when (category) {
            "Activity" -> R.color.pillars_activities
            "Service" -> R.color.pillars_services
            "Receiver" -> R.color.pillars_receivers
            "Provider" -> R.color.pillars_providers
            else -> android.R.color.white
        }
        val status = if (success) "✓" else "✗"
        val logEntry = "$timestamp [$status] $category: $message\n"
        binding.tvLog.append(logEntry)
        
        // No need to scroll since the TextView will expand
    }

    companion object {
        private var instance: PillarsInteractiveActivity? = null
        private const val CONTACTS_PERMISSION_REQUEST_CODE = 100

        fun getInstance(): PillarsInteractiveActivity? = instance
    }
} 
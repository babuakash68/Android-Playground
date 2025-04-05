package com.example.androidplayground.activities.learning.interactive.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityFragmentInteractiveBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class FragmentInteractiveActivity : AppCompatActivity(), FragmentInteractiveActivity.FragmentLogger {
    private lateinit var binding: ActivityFragmentInteractiveBinding
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val logBuilder = StringBuilder()
    private var currentFragment: Fragment? = null
    private var fragmentCounter = 1
    private val fragmentStack = mutableListOf<Fragment>()

    companion object {
        private const val TAG = "FragmentInteractive"
    }

    interface FragmentLogger {
        fun logFragmentEvent(message: String, isSuccess: Boolean = true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentInteractiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActivity()
    }

    private fun setupActivity() {
        setupToolbar()
        setupButtons()
        setupBackStackSwitch()
        updateFragmentCounterDisplay()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupButtons() {
        binding.apply {
            addFragmentButton.setOnClickListener { addFragment() }
            removeFragmentButton.setOnClickListener { removeFragment() }
            replaceFragmentButton.setOnClickListener { replaceFragment() }
            resetButton.setOnClickListener { resetDemo() }
            copyLogButton.setOnClickListener { copyLogToClipboard() }
        }
    }

    private fun setupBackStackSwitch() {
        binding.backStackSwitch.setOnCheckedChangeListener { _, isChecked ->
            addLogEntry("Back Stack ${if (isChecked) "enabled" else "disabled"}", true)
        }
    }

    private fun updateFragmentCounterDisplay() {
        val backStackCount = supportFragmentManager.backStackEntryCount
        val totalFragments = fragmentStack.size
        addLogEntry("Fragment counter: $fragmentCounter, Back stack: $backStackCount, Total fragments: $totalFragments", true)
    }

    private fun addFragment() {
        // Always create a new fragment, regardless of whether one exists
        val newFragment = DemoFragment.newInstance("Fragment $fragmentCounter")
        fragmentCounter++
        fragmentStack.add(newFragment)
        
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_left
            )
            .add(R.id.fragmentContainer, newFragment)
        
        if (binding.backStackSwitch.isChecked) {
            transaction.addToBackStack(null)
        }
        
        transaction.commit()
        
        // Update current fragment reference
        currentFragment = newFragment
        
        addLogEntry("Fragment added${if (binding.backStackSwitch.isChecked) " to back stack" else ""}", true)
        updateFragmentCounterDisplay()
    }

    private fun removeFragment() {
        currentFragment?.let {
            // Remove from the fragment manager
            val transaction = supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.slide_out_left
                )
                .remove(it)
            
            // Don't add to back stack when removing
            transaction.commit()
            
            // Remove from our tracking list
            fragmentStack.remove(it)
            
            // Update current fragment reference
            currentFragment = null
            
            // If there are fragments in the back stack, get the last one
            if (fragmentStack.isNotEmpty()) {
                currentFragment = fragmentStack.lastOrNull()
            }
            
            addLogEntry("Fragment removed", true)
            updateFragmentCounterDisplay()
        } ?: run {
            addLogEntry("No fragment to remove", false)
        }
    }

    private fun replaceFragment() {
        val newFragment = DemoFragment.newInstance("Fragment $fragmentCounter")
        fragmentCounter++
        
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            .replace(R.id.fragmentContainer, newFragment)
        
        if (binding.backStackSwitch.isChecked) {
            transaction.addToBackStack(null)
        }
        
        transaction.commit()
        
        // Update the fragment stack
        currentFragment?.let { fragmentStack.remove(it) }
        currentFragment = newFragment
        fragmentStack.add(newFragment)
        
        addLogEntry("Fragment replaced${if (binding.backStackSwitch.isChecked) " and added to back stack" else ""}", true)
        updateFragmentCounterDisplay()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            
            // Update current fragment reference
            if (fragmentStack.isNotEmpty()) {
                fragmentStack.removeAt(fragmentStack.size - 1)
                currentFragment = fragmentStack.lastOrNull()
            } else {
                currentFragment = null
            }
            
            addLogEntry("Back stack popped", true)
            updateFragmentCounterDisplay()
        } else {
            super.onBackPressed()
        }
    }

    fun addLogEntry(message: String, isSuccess: Boolean) {
        val timestamp = dateFormat.format(Date())
        val color = if (isSuccess) "#4CAF50" else "#F44336"
        val fragmentInfo = if (currentFragment != null) {
            val fragmentName = (currentFragment as? DemoFragment)?.getFragmentName() ?: "Unknown"
            " [$fragmentName]"
        } else ""
        logBuilder.append("<font color='$color'>[$timestamp]$fragmentInfo $message</font><br>")
        binding.logText.text = android.text.Html.fromHtml(logBuilder.toString(), android.text.Html.FROM_HTML_MODE_COMPACT)
    }

    override fun logFragmentEvent(message: String, isSuccess: Boolean) {
        addLogEntry(message, isSuccess)
    }

    private fun copyLogToClipboard() {
        // Strip HTML tags and convert to plain text
        val plainText = logBuilder.toString()
            .replace(Regex("<[^>]*>"), "") // Remove HTML tags
            .replace("&nbsp;", " ") // Replace HTML spaces
            .replace("&lt;", "<") // Replace HTML entities
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .trim() // Remove extra whitespace
        
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Fragment Log", plainText)
        clipboard.setPrimaryClip(clip)
        addLogEntry("Log copied to clipboard", true)
    }

    private fun resetDemo() {
        // Clear all fragments from the fragment manager
        supportFragmentManager.popBackStackImmediate(null, supportFragmentManager.backStackEntryCount)
        
        // Clear our tracking
        currentFragment = null
        fragmentStack.clear()
        logBuilder.clear()
        binding.logText.text = ""
        addLogEntry("Demo reset", true)
        updateFragmentCounterDisplay()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity destroyed")
    }
} 
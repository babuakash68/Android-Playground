package com.example.androidplayground.activities.learning.interactive.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.androidplayground.R
import com.example.androidplayground.databinding.ActivityViewsInteractiveBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class ViewsInteractiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewsInteractiveBinding
    private lateinit var toolbar: Toolbar
    private lateinit var canvasStatus: TextView
    private lateinit var canvasContainer: FrameLayout
    private lateinit var logText: TextView
    private lateinit var clearButton: Button
    private lateinit var currentLayout: ViewGroup
    private var selectedView: View? = null
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val handler = Handler(Looper.getMainLooper())
    private var showLayoutBounds = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewsInteractiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        setupToolbar()
        setupCanvas()
        setupButtons()
        showWelcomeMessage()
    }

    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        canvasStatus = findViewById(R.id.canvasStatus)
        canvasContainer = findViewById(R.id.canvasContainer)
        logText = findViewById(R.id.logText)
        clearButton = findViewById(R.id.clearButton)

        // Initialize currentLayout with a default LinearLayout
        currentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.color.purple_50)
        }
        canvasContainer.addView(currentLayout)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("topic_title") ?: "Views Interactive"
    }

    private fun setupCanvas() {
        updateCanvasStatus("Ready")
    }

    private fun setupButtons() {
        // Add TextView Button
        binding.addTextViewButton.setOnClickListener {
            addTextView()
        }

        // Add Button Button
        binding.addButtonButton.setOnClickListener {
            addButton()
        }

        // Add ImageView Button
        binding.addImageViewButton.setOnClickListener {
            addImageView()
        }

        // Add EditText Button
        binding.addEditTextButton.setOnClickListener {
            addEditText()
        }

        // Add CheckBox Button
        binding.addCheckBoxButton.setOnClickListener {
            addCheckBox()
        }

        // Add RadioButton Button
        binding.addRadioButtonButton.setOnClickListener {
            addRadioButton()
        }

        // Add ProgressBar Button
        binding.addProgressBarButton.setOnClickListener {
            addProgressBar()
        }

        // Layout Switch Buttons
        binding.switchLinearLayoutButton.setOnClickListener {
            switchToLinearLayout()
        }

        binding.switchRelativeLayoutButton.setOnClickListener {
            switchToRelativeLayout()
        }

        binding.switchConstraintLayoutButton.setOnClickListener {
            switchToConstraintLayout()
        }

        // Layout Tools
        binding.addNestedLayoutButton.setOnClickListener {
            addNestedLayout()
        }

        binding.clearCanvasButton.setOnClickListener {
            clearCanvas()
        }

        // Learning Tools
        binding.showLayoutBoundsButton.setOnClickListener {
            toggleLayoutBounds()
        }

        binding.showViewTreeButton.setOnClickListener {
            showViewTree()
        }

        // Customize View Button
        binding.customizeViewButton.setOnClickListener {
            customizeSelectedView()
        }

        // Inspect View Button
        binding.inspectViewButton.setOnClickListener {
            inspectSelectedView()
        }

        // Break It Button
        binding.breakItButton.setOnClickListener {
            breakLayout()
        }

        // Clear Log Button
        clearButton.setOnClickListener {
            logText.text = ""
            log("Log cleared", LogType.INFO)
        }

        // Copy Log Button
        binding.copyButton.setOnClickListener {
            copyLogToClipboard()
        }
    }

    private fun addTextView() {
        val textView = TextView(this).apply {
            text = "Edit me!"
            setTextColor(Color.BLACK)
            setBackgroundResource(R.color.purple_200)
            setPadding(16, 8, 16, 8)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { selectView(this) }
        }

        addViewToCanvas(textView)
        log("Added TextView - Edit me!", LogType.VIEW)
        log("Tip: Shows text or input!", LogType.TIP)
    }

    private fun addButton() {
        val button = Button(this).apply {
            text = "Click me!"
            setTextColor(Color.WHITE)
            setBackgroundResource(R.color.purple_500)
            setPadding(16, 8, 16, 8)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { 
                selectView(this)
                showSnackbar("Button clicked!")
            }
        }

        addViewToCanvas(button)
        log("Added Button - Click me!", LogType.VIEW)
        log("Tip: Handles user interaction!", LogType.TIP)
    }

    private fun addImageView() {
        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.ic_launcher_foreground)
            setBackgroundResource(R.color.purple_200)
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { selectView(this) }
        }

        addViewToCanvas(imageView)
        log("Added ImageView", LogType.VIEW)
        log("Tip: Displays images or drawables!", LogType.TIP)
    }

    private fun addEditText() {
        val editText = EditText(this).apply {
            hint = "Type here..."
            setTextColor(Color.BLACK)
            setBackgroundResource(R.color.white)
            setPadding(16, 8, 16, 8)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { selectView(this) }
        }

        addViewToCanvas(editText)
        log("Added EditText - Type here...", LogType.VIEW)
        log("Tip: Handles text input!", LogType.TIP)
    }

    private fun addCheckBox() {
        val checkBox = CheckBox(this).apply {
            text = "Check me!"
            setTextColor(Color.BLACK)
            setPadding(16, 8, 16, 8)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { 
                selectView(this)
                showSnackbar("CheckBox ${if (isChecked) "checked" else "unchecked"}!")
            }
        }

        addViewToCanvas(checkBox)
        log("Added CheckBox - Check me!", LogType.VIEW)
        log("Tip: Handles boolean selection!", LogType.TIP)
    }

    private fun addRadioButton() {
        val radioButton = RadioButton(this).apply {
            text = "Select me!"
            setTextColor(Color.BLACK)
            setPadding(16, 8, 16, 8)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { 
                selectView(this)
                showSnackbar("RadioButton ${if (isChecked) "selected" else "unselected"}!")
            }
        }

        addViewToCanvas(radioButton)
        log("Added RadioButton - Select me!", LogType.VIEW)
        log("Tip: Part of RadioGroup for single selection!", LogType.TIP)
    }

    private fun addProgressBar() {
        val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            progress = 50
            setBackgroundResource(R.color.purple_100)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { selectView(this) }
        }

        addViewToCanvas(progressBar)
        log("Added ProgressBar", LogType.VIEW)
        log("Tip: Shows progress or loading state!", LogType.TIP)
    }

    private fun switchToLinearLayout() {
        val newLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.color.purple_50)
        }

        switchLayout(newLayout)
        log("Switched to LinearLayout", LogType.LAYOUT)
        log("Tip: LinearLayout arranges views in a line!", LogType.TIP)
    }

    private fun switchToRelativeLayout() {
        val newLayout = RelativeLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.color.purple_50)
        }

        switchLayout(newLayout)
        log("Switched to RelativeLayout", LogType.LAYOUT)
        log("Tip: RelativeLayout positions views relative to each other!", LogType.TIP)
    }

    private fun switchToConstraintLayout() {
        val newLayout = ConstraintLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.color.purple_50)
        }

        switchLayout(newLayout)
        log("Switched to ConstraintLayout", LogType.LAYOUT)
        log("Tip: ConstraintLayout is flexible and powerful!", LogType.TIP)
    }

    private fun switchLayout(newLayout: ViewGroup) {
        try {
            // Copy all children from current layout to new layout
            val children = mutableListOf<View>()
            for (i in 0 until currentLayout.childCount) {
                children.add(currentLayout.getChildAt(i))
            }

            // Remove all children from current layout
            currentLayout.removeAllViews()

            // Add all children to new layout
            for (child in children) {
                newLayout.addView(child)
            }

            // Replace current layout with new layout
            canvasContainer.removeAllViews()
            canvasContainer.addView(newLayout)
            currentLayout = newLayout

            // Update layout bounds if they are shown
            if (showLayoutBounds) {
                updateLayoutBounds()
            }

            log("Layout switched to ${newLayout.javaClass.simpleName}", LogType.ACTION)
        } catch (e: Exception) {
            log("Error switching layout: ${e.message}", LogType.ERROR)
            showSnackbar("Error switching layout")
        }
    }

    private fun addNestedLayout() {
        val nestedLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.color.purple_100)
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { selectView(this) }
        }

        // Add some sample views to the nested layout
        repeat(3) { i ->
            val textView = TextView(this).apply {
                text = "Nested View $i"
                setTextColor(Color.BLACK)
                setBackgroundResource(R.color.white)
                setPadding(16, 8, 16, 8)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
            }
            nestedLayout.addView(textView)
        }

        addViewToCanvas(nestedLayout)
        log("Added Nested Layout", LogType.LAYOUT)
        log("Tip: Layouts can contain other layouts!", LogType.TIP)
    }

    private fun clearCanvas() {
        try {
            currentLayout.removeAllViews()
            selectedView = null
            log("Canvas cleared", LogType.ACTION)
        } catch (e: Exception) {
            log("Error clearing canvas: ${e.message}", LogType.ERROR)
            showSnackbar("Error clearing canvas")
        }
    }

    private fun toggleLayoutBounds() {
        showLayoutBounds = !showLayoutBounds
        updateLayoutBounds()
        log("Layout bounds ${if (showLayoutBounds) "shown" else "hidden"}", LogType.ACTION)
    }

    private fun updateLayoutBounds() {
        fun updateViewBounds(view: View) {
            if (showLayoutBounds) {
                view.background = ColorDrawable(Color.argb(50, 0, 0, 255))
            } else {
                view.background = null
            }

            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    updateViewBounds(view.getChildAt(i))
                }
            }
        }

        updateViewBounds(currentLayout)
    }

    private fun showViewTree() {
        fun buildViewTree(view: View, depth: Int = 0): String {
            val indent = "  ".repeat(depth)
            val builder = StringBuilder()
            builder.append("$indent${view.javaClass.simpleName}\n")

            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    builder.append(buildViewTree(view.getChildAt(i), depth + 1))
                }
            }

            return builder.toString()
        }

        val viewTree = buildViewTree(currentLayout)
        showSnackbar("View Tree generated - Check log for details")
        log("View Tree:\n$viewTree", LogType.INFO)
    }

    private fun customizeSelectedView() {
        selectedView?.let { view ->
            // Show a dialog with customization options
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Customize View")
                .setMessage("Select a customization option:")
                .setItems(arrayOf("Change Text", "Change Color", "Change Size")) { _, which ->
                    when (which) {
                        0 -> changeViewText(view)
                        1 -> changeViewColor(view)
                        2 -> changeViewSize(view)
                    }
                }
                .create()
            
            dialog.show()
        } ?: run {
            showSnackbar("Select a view first!")
        }
    }

    private fun changeViewText(view: View) {
        if (view is TextView) {
            try {
                val input = EditText(this).apply {
                    setText(view.text)
                    setSingleLine()
                    setPadding(16, 8, 16, 8)
                }
                
                MaterialAlertDialogBuilder(this)
                    .setTitle("Change Text")
                    .setView(input)
                    .setPositiveButton("OK") { _, _ ->
                        try {
                            view.text = input.text
                            log("Changed text of ${view.javaClass.simpleName}", LogType.ACTION)
                        } catch (e: Exception) {
                            log("Error changing text: ${e.message}", LogType.ERROR)
                            showSnackbar("Error changing text")
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } catch (e: Exception) {
                log("Error creating text dialog: ${e.message}", LogType.ERROR)
                showSnackbar("Error creating dialog")
            }
        } else {
            showSnackbar("This view doesn't support text!")
        }
    }

    private fun changeViewColor(view: View) {
        try {
            val colors = arrayOf(
                "Red" to Color.RED,
                "Green" to Color.GREEN,
                "Blue" to Color.BLUE,
                "Yellow" to Color.YELLOW,
                "Purple" to ContextCompat.getColor(this, R.color.purple_500)
            )
            
            MaterialAlertDialogBuilder(this)
                .setTitle("Change Color")
                .setItems(colors.map { it.first }.toTypedArray()) { _, which ->
                    try {
                        view.setBackgroundColor(colors[which].second)
                        log("Changed color of ${view.javaClass.simpleName}", LogType.ACTION)
                    } catch (e: Exception) {
                        log("Error changing color: ${e.message}", LogType.ERROR)
                        showSnackbar("Error changing color")
                    }
                }
                .show()
        } catch (e: Exception) {
            log("Error creating color dialog: ${e.message}", LogType.ERROR)
            showSnackbar("Error creating dialog")
        }
    }

    private fun changeViewSize(view: View) {
        try {
            val sizes = arrayOf(
                "Small" to Pair(100, 50),
                "Medium" to Pair(200, 100),
                "Large" to Pair(300, 150)
            )
            
            MaterialAlertDialogBuilder(this)
                .setTitle("Change Size")
                .setItems(sizes.map { it.first }.toTypedArray()) { _, which ->
                    try {
                        val (width, height) = sizes[which].second
                        view.layoutParams = ViewGroup.LayoutParams(width, height)
                        view.requestLayout()
                        log("Changed size of ${view.javaClass.simpleName}", LogType.ACTION)
                    } catch (e: Exception) {
                        log("Error changing size: ${e.message}", LogType.ERROR)
                        showSnackbar("Error changing size")
                    }
                }
                .show()
        } catch (e: Exception) {
            log("Error creating size dialog: ${e.message}", LogType.ERROR)
            showSnackbar("Error creating dialog")
        }
    }

    private fun inspectSelectedView() {
        selectedView?.let { view ->
            val parent = view.parent as? ViewGroup
            val hierarchy = StringBuilder()
            var current: View? = view
            
            while (current != null) {
                hierarchy.insert(0, "\n${current.javaClass.simpleName}")
                current = current.parent as? View
            }
            
            log("View Hierarchy:$hierarchy", LogType.INFO)
            showSnackbar("View inspected - Check log for details")
        } ?: run {
            showSnackbar("Select a view first!")
        }
    }

    private fun breakLayout() {
        selectedView?.let { view ->
            try {
                val params = view.layoutParams
                params.width = 0
                params.height = 0
                view.layoutParams = params
                
                log("Layout broken - View size set to 0!", LogType.ERROR)
                log("Tip: This shows how incorrect layout params can break the UI", LogType.TIP)
            } catch (e: Exception) {
                log("Error breaking layout: ${e.message}", LogType.ERROR)
                showSnackbar("Error breaking layout")
            }
        } ?: run {
            showSnackbar("Select a view first!")
        }
    }

    private fun addViewToCanvas(view: View) {
        try {
            currentLayout.addView(view)
            animateViewIn(view)
            selectView(view)
        } catch (e: Exception) {
            log("Error adding view to canvas: ${e.message}", LogType.ERROR)
            showSnackbar("Error adding view")
        }
    }

    private fun animateViewIn(view: View) {
        try {
            view.alpha = 0f
            view.translationY = 100f
            
            val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }
            
            val translationAnimator = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }
            
            alphaAnimator.start()
            translationAnimator.start()
            
            // Clean up animations when view is removed
            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {}
                override fun onViewDetachedFromWindow(v: View) {
                    alphaAnimator.cancel()
                    translationAnimator.cancel()
                    v.removeOnAttachStateChangeListener(this)
                }
            })
        } catch (e: Exception) {
            log("Error animating view: ${e.message}", LogType.ERROR)
        }
    }

    private fun selectView(view: View) {
        try {
            selectedView?.setBackgroundColor(Color.TRANSPARENT)
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
            selectedView = view
            log("View selected: ${view.javaClass.simpleName}", LogType.INFO)
        } catch (e: Exception) {
            log("Error selecting view: ${e.message}", LogType.ERROR)
        }
    }

    private fun updateCanvasStatus(status: String) {
        canvasStatus.text = "Canvas Status: $status"
    }

    private fun showWelcomeMessage() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Welcome to Views Interactive!")
            .setMessage("This interactive tool helps you learn about Android Views and Layouts.\n\n" +
                    "Try adding different views and layouts to see how they work!")
            .setPositiveButton("Got it!") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun copyLogToClipboard() {
        try {
            val logText = binding.logText.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("View Log", logText)
            clipboard.setPrimaryClip(clip)
            showSnackbar("Log copied to clipboard")
        } catch (e: Exception) {
            log("Error copying log: ${e.message}", LogType.ERROR)
            showSnackbar("Error copying log")
        }
    }

    private fun log(message: String, type: LogType) {
        val timestamp = dateFormat.format(Date())
        val prefix = when (type) {
            LogType.INFO -> "[INFO]"
            LogType.VIEW -> "[VIEW]"
            LogType.LAYOUT -> "[LAYOUT]"
            LogType.ACTION -> "[ACTION]"
            LogType.ERROR -> "[ERROR]"
            LogType.TIP -> "[TIP]"
        }
        
        val logMessage = "$timestamp $prefix $message\n"
        logText.append(logMessage)
    }

    private enum class LogType {
        INFO, VIEW, LAYOUT, ACTION, ERROR, TIP
    }

    private fun createComplexLayout(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.color.purple_50)
        }

        // Add multiple nested layouts based on screen size
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val numNestedLayouts = minOf(5, screenHeight / 200) // Adjust based on screen height

        repeat(numNestedLayouts) { i ->
            val nestedLayout = LinearLayout(this).apply {
                orientation = if (i % 2 == 0) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
                setBackgroundResource(R.color.purple_100)
            }

            val numViews = minOf(5, screenWidth / 200) // Adjust based on screen width
            repeat(numViews) { j ->
                val view = TextView(this).apply {
                    text = "View $i-$j"
                    setTextColor(Color.BLACK)
                    setBackgroundResource(R.color.purple_200)
                    setPadding(16, 8, 16, 8)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                    }
                }
                nestedLayout.addView(view)
            }

            root.addView(nestedLayout)
        }

        return root
    }
} 
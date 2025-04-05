package com.example.androidplayground.activities.learning.interactive.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidplayground.R
import com.example.androidplayground.databinding.FragmentDemoBinding
import kotlin.random.Random

class DemoFragment : Fragment() {
    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!
    private var fragmentName: String = ""
    private var fragmentLogger: FragmentInteractiveActivity.FragmentLogger? = null
    private var fragmentColor: Int = Color.WHITE

    fun getFragmentName(): String = fragmentName

    companion object {
        private const val ARG_FRAGMENT_NAME = "fragment_name"
        private const val ARG_FRAGMENT_COLOR = "fragment_color"
        private const val TAG = "DemoFragment"

        fun newInstance(name: String, color: Int = getRandomColor()): DemoFragment {
            return DemoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FRAGMENT_NAME, name)
                    putInt(ARG_FRAGMENT_COLOR, color)
                }
            }
        }

        private fun getRandomColor(): Int {
            val colors = arrayOf(
                Color.rgb(255, 182, 193), // Light pink
                Color.rgb(173, 216, 230), // Light blue
                Color.rgb(144, 238, 144), // Light green
                Color.rgb(255, 218, 185), // Peach
                Color.rgb(221, 160, 221), // Plum
                Color.rgb(255, 255, 224), // Light yellow
                Color.rgb(176, 224, 230), // Powder blue
                Color.rgb(255, 192, 203)  // Pink
            )
            return colors[Random.nextInt(colors.size)]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentName = arguments?.getString(ARG_FRAGMENT_NAME) ?: "Unknown Fragment"
        fragmentColor = arguments?.getInt(ARG_FRAGMENT_COLOR, Color.WHITE) ?: Color.WHITE
        fragmentLogger = activity as? FragmentInteractiveActivity
        Log.d(TAG, "$fragmentName: onCreate()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onCreate()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        Log.d(TAG, "$fragmentName: onCreateView()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onCreateView()")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentNameText.text = fragmentName
        binding.root.setBackgroundColor(fragmentColor)
        Log.d(TAG, "$fragmentName: onViewCreated()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onViewCreated()")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "$fragmentName: onStart()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "$fragmentName: onResume()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "$fragmentName: onPause()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "$fragmentName: onStop()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onStop()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "$fragmentName: onDestroyView()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onDestroyView()")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "$fragmentName: onDestroy()")
        fragmentLogger?.logFragmentEvent("$fragmentName: onDestroy()")
    }
} 
package com.example.androidplayground.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidplayground.R
import com.example.androidplayground.activities.learning.ActivityTheoryActivity
import com.example.androidplayground.activities.learning.FragmentTheoryActivity
import com.example.androidplayground.activities.learning.TheoryActivity
import com.example.androidplayground.activities.learning.interactive.LifecycleInteractiveActivity
import com.example.androidplayground.activities.learning.interactive.context.ContextInteractiveActivity
import com.example.androidplayground.activities.learning.interactive.fragment.FragmentInteractiveActivity
import com.example.androidplayground.databinding.ItemTopicBinding
import com.example.androidplayground.models.Topic

class TopicAdapter : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    private val topics = listOf(
        Topic(
            "Activity Lifecycle",
            "Learn about the Android Activity lifecycle and its states",
            R.drawable.ic_activity,
            ActivityTheoryActivity::class.java,
            LifecycleInteractiveActivity::class.java
        ),
        Topic(
            "Fragment Lifecycle",
            "Understand Fragment lifecycle and its interaction with Activities",
            R.drawable.ic_fragment,
            FragmentTheoryActivity::class.java,
            FragmentInteractiveActivity::class.java
        ),
        Topic(
            "Context",
            "Explore different types of Context in Android",
            R.drawable.ic_context,
            TheoryActivity::class.java,
            ContextInteractiveActivity::class.java
        ),
        Topic(
            "Four Pillars of Android",
            "Learn about the four fundamental components of Android",
            R.drawable.ic_pillars,
            TheoryActivity::class.java,
            ContextInteractiveActivity::class.java
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int = topics.size

    class TopicViewHolder(private val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic) {
            binding.apply {
                tvTitle.text = topic.title
                tvDescription.text = topic.description
                ivIcon.setImageResource(topic.iconResId)
            }
        }
    }
} 
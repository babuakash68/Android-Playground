package com.example.androidplayground.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androidplayground.R
import com.example.androidplayground.models.IntentTopic

class IntentTopicAdapter(
    private val topics: List<IntentTopic>,
    private val onTopicClick: (IntentTopic) -> Unit
) : RecyclerView.Adapter<IntentTopicAdapter.TopicViewHolder>() {

    private val expandedItems = mutableSetOf<String>()

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivTopicIcon: ImageView = itemView.findViewById(R.id.ivTopicIcon)
        private val tvTopicTitle: TextView = itemView.findViewById(R.id.tvTopicTitle)
        private val tvTopicDescription: TextView = itemView.findViewById(R.id.tvTopicDescription)
        private val tvTopicTheory: TextView = itemView.findViewById(R.id.tvTopicTheory)
        private val btnExpand: ImageButton = itemView.findViewById(R.id.btnExpand)

        fun bind(topic: IntentTopic) {
            ivTopicIcon.setImageResource(topic.iconResId)
            ivTopicIcon.setColorFilter(ContextCompat.getColor(itemView.context, topic.colorResId))
            
            tvTopicTitle.text = topic.title
            tvTopicDescription.text = topic.description
            tvTopicTheory.text = topic.theory
            
            // Set expanded state
            val isExpanded = expandedItems.contains(topic.id)
            tvTopicTheory.visibility = if (isExpanded) View.VISIBLE else View.GONE
            btnExpand.rotation = if (isExpanded) 180f else 0f
            
            // Set click listeners
            itemView.setOnClickListener {
                onTopicClick(topic)
            }
            
            btnExpand.setOnClickListener {
                if (expandedItems.contains(topic.id)) {
                    expandedItems.remove(topic.id)
                } else {
                    expandedItems.add(topic.id)
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_intent_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int = topics.size
} 
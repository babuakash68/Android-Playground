package com.example.androidplayground.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidplayground.R
import com.example.androidplayground.data.model.AndroidTopic
import com.example.androidplayground.utli.BaseRvAdapter

class AndroidTopicAdapter(
    context: android.content.Context,
    private val onTopicClick: (AndroidTopic) -> Unit
) : BaseRvAdapter<AndroidTopic, AndroidTopicAdapter.TopicViewHolder>(
    context = context,
    layoutId = R.layout.item_android_topic,
    holderMaker = { view -> TopicViewHolder(view) },
    mutableList = mutableListOf(),
    binder = { holder, topic, _ -> holder.bind(topic, onTopicClick) }
) {

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.topicIcon)
        private val title: TextView = itemView.findViewById(R.id.topicTitle)
        private val description: TextView = itemView.findViewById(R.id.topicDescription)

        fun bind(topic: AndroidTopic, onTopicClick: (AndroidTopic) -> Unit) {
            icon.setImageResource(topic.iconResId)
            title.text = topic.title
            description.text = topic.description
            itemView.setOnClickListener { onTopicClick(topic) }
        }
    }
} 
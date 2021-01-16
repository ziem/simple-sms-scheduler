package com.github.ziem.simplesmsscheduler.feature.messages

import androidx.recyclerview.widget.DiffUtil
import com.github.ziem.simplesmsscheduler.model.Message

object MainDiffUtil : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return if (oldItem is Message && newItem is Message) {
            oldItem.id == newItem.id
        } else if (oldItem is Title && newItem is Title) {
            oldItem.text == newItem.text
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return if (oldItem is Message && newItem is Message) {
            oldItem == newItem
        } else if (oldItem is Title && newItem is Title) {
            oldItem == newItem
        } else {
            false
        }
    }
}
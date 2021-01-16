package com.github.ziem.simplesmsscheduler.feature.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.ziem.simplesmsscheduler.R
import com.github.ziem.simplesmsscheduler.databinding.ItemMessageBinding
import com.github.ziem.simplesmsscheduler.databinding.ItemTitleBinding
import com.github.ziem.simplesmsscheduler.model.Message
import javax.inject.Inject

class RecyclerViewAdapter @Inject constructor(
) : ListAdapter<Any, RecyclerView.ViewHolder>(MainDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.item_message) {
            MessageViewHolder(
                ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            TitleViewHolder(
                ItemTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is Message) {
            R.layout.item_message
        } else {
            R.layout.item_title
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MessageViewHolder) {
            holder.bind(getItem(position) as Message)
        } else if (holder is TitleViewHolder) {
            holder.bind(getItem(position) as Title)
        }
    }
}

package com.github.ziem.simplesmsscheduler.feature.messages

import androidx.recyclerview.widget.RecyclerView
import com.github.ziem.simplesmsscheduler.databinding.ItemTitleBinding

class TitleViewHolder(
    private val binding: ItemTitleBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(title: Title) {
        binding.root.text = title.text
    }
}
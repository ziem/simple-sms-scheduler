package com.github.ziem.simplesmsscheduler.feature.messages

import android.widget.Toast
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.ziem.simplesmsscheduler.R
import com.github.ziem.simplesmsscheduler.databinding.ItemMessageBinding
import com.github.ziem.simplesmsscheduler.feature.add_edit_message.AddEditMessageFragmentArgs
import com.github.ziem.simplesmsscheduler.model.Message
import com.github.ziem.simplesmsscheduler.model.State
import com.jakewharton.rxbinding3.view.clicks
import org.threeten.bp.format.DateTimeFormatter

class MessageViewHolder(
    private val binding: ItemMessageBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm, dd.MM.yyyy")

    private lateinit var currentMessage: Message

    fun bind(message: Message) {
        currentMessage = message
        binding.dateTime.text = dateTimeFormatter.format(message.dateTime)
        binding.message.text = message.message
        binding.chip.text = message.contactName

        val stateImage = when (message.state) {
            State.Scheduled -> R.drawable.ic_clock_24dp
            State.Sent -> R.drawable.ic_check_circle_24dp
            State.Unknown -> R.drawable.ic_sms_failed_24dp
        }
        binding.state.setImageResource(stateImage)
        binding.state.setOnClickListener {
            val toastMessage = when (message.state) {
                State.Scheduled -> "This message was scheduled successfully"
                State.Sent -> "This message was sent successfully"
                State.Unknown -> "This message failed for the unknown reason"
            }

            Toast.makeText(itemView.context, toastMessage, Toast.LENGTH_SHORT).show()
        }

        val alpha = if (message.state == State.Sent) {
            0.4f
        } else {
            1f
        }
        binding.root.children.forEach { it.alpha = alpha }

        itemView.clicks().subscribe {
            itemView.findNavController().navigate(
                R.id.action_messagesFragment_to_addEditMessageFragment,
                AddEditMessageFragmentArgs(message).toBundle()
            )
        }
    }
}
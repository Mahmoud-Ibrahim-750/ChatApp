package com.mis.route.chatapp.ui.fragments.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mis.route.chatapp.data.firebase.auth.FirebaseAuth
import com.mis.route.chatapp.databinding.ItemReceivedMessageBinding
import com.mis.route.chatapp.databinding.ItemSentMessageBinding
import com.mis.route.chatapp.ui.model.Message

class MessagesAdapter(
    var messagesList: MutableList<Message>?
) : RecyclerView.Adapter<ViewHolder>() {

    class SentViewHolder(val binding: ItemSentMessageBinding) : ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.content.text = message.content
            binding.time.text = message.getFormattedTime() ?: ""
        }
    }

    class ReceivedViewHolder(val binding: ItemReceivedMessageBinding) : ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.content.text = message.content
            binding.time.text = message.getFormattedTime() ?: ""
            binding.senderName.text = message.senderName
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messagesList?.get(position)?.senderId == FirebaseAuth.getCurrentAuthUser()?.uid)
            ViewHolderType.Sent.value else ViewHolderType.Received.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == ViewHolderType.Sent.value) {
            val binding = ItemSentMessageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            SentViewHolder(binding)
        } else {
            val binding = ItemReceivedMessageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedViewHolder(binding)
        }
    }

    override fun getItemCount() = messagesList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (messagesList == null) return
        when (holder) {
            is SentViewHolder -> {
                holder.bind(messagesList!![position])
            }

            is ReceivedViewHolder -> {
                holder.bind(messagesList!![position])
            }
        }
    }

}
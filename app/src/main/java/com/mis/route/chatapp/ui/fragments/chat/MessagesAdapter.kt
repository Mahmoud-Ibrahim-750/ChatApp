package com.mis.route.chatapp.ui.fragments.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mis.route.chatapp.databinding.ItemMessageBinding
import com.mis.route.chatapp.ui.model.Message

class MessagesAdapter(var messagesList: MutableList<Message>?) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = messagesList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.message = messagesList?.get(position)
    }

}
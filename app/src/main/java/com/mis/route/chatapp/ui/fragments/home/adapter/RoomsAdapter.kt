package com.mis.route.chatapp.ui.fragments.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mis.route.chatapp.databinding.ItemRoomBinding
import com.mis.route.chatapp.ui.fragments.createroom.model.Room

class RoomsAdapter(var roomsList: List<Room>?) : RecyclerView.Adapter<RoomsAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        // TODO: implement the joining status later
//        fun bindJoinStatus(room: Room) {
//            if (room.membersIds.contains(room.))
//            binding.joinButton.text =
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = roomsList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = roomsList?.get(position) ?: Room()
        holder.binding.room = room
        holder.binding.root.setOnClickListener {
            onRoomClickListener?.onRoomClicked(room)
        }
    }

    var onRoomClickListener: OnRoomClickListener? = null
    fun interface OnRoomClickListener {
        fun onRoomClicked(room: Room)
    }
}
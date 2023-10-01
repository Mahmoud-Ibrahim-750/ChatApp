package com.mis.route.chatapp.ui.fragments.chat

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mis.route.chatapp.data.firebase.DataConstants
import com.mis.route.chatapp.databinding.FragmentChatBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.UiConstants
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.model.Message

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()
    private var room: Room? = null
    private lateinit var messagesListenerReg: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            chatFragment = this@ChatFragment
        }
        getRoom()
        initRecyclerView()
        messagesListenerReg = observeFirestoreChanges()
    }

    private fun observeFirestoreChanges() : ListenerRegistration {
        val collRef = Firebase.firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(room?.id!!)
            .collection(DataConstants.MESSAGES_COLLECTION)

        return collRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot == null) return@addSnapshotListener

            for (doc in snapshot.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        showAddedMessages(mutableListOf(doc.document.toObject(Message::class.java)))
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun getRoom() {
        arguments?.let {
            room = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(UiConstants.PASSED_ROOM, Room::class.java)
            } else {
                it.getParcelable(UiConstants.PASSED_ROOM)
            }
        }
    }

    private fun initRecyclerView() {
        val adapter = MessagesAdapter(null)
        binding.messagesRecycler.adapter = adapter
        loadMessages()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadMessages() {
        room?.let {
            (binding.messagesRecycler.adapter as MessagesAdapter).apply {
                messagesList = sharedViewModel.getMessages(room!!.id!!)
                notifyDataSetChanged()
            }
        }
    }

    fun sendMessage() {
        room?.id?.let {
            val message = Message(
                binding.messageInput.text.toString(),
                sharedViewModel.getCurrentUser()?.uid,
                it,
                Timestamp.now()
                )
            sharedViewModel.sendMessage(message)
        }

    }

    private fun showAddedMessages(newMessages: MutableList<Message>) {
        (binding.messagesRecycler.adapter as MessagesAdapter).apply {
            messagesList?.addAll(newMessages)
            val startPosition = messagesList!!.size - newMessages.size
            notifyItemRangeInserted(startPosition, newMessages.size)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        messagesListenerReg.remove() // stop observing messages real-time updates
    }
}
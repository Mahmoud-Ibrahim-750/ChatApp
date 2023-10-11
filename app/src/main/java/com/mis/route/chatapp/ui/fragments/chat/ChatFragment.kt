package com.mis.route.chatapp.ui.fragments.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mis.route.chatapp.data.firebase.DataConstants
import com.mis.route.chatapp.data.firebase.auth.FirebaseAuth
import com.mis.route.chatapp.databinding.FragmentChatBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.model.Message

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment(private val room: Room) : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()
    private lateinit var messagesListenerRegistration: ListenerRegistration

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
        initRecyclerView()
        observeLiveData()
        messagesListenerRegistration = observeFirestoreChanges()
    }

    private fun observeLiveData() {
        sharedViewModel.messages.observe(viewLifecycleOwner, ::handleMessagesLoading)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleMessagesLoading(messages: MutableList<Message>) {
        if (messages.isEmpty()) toggleRecyclerView(false)
        else {
            toggleRecyclerView(true)
            (binding.messagesRecycler.adapter as MessagesAdapter).apply {
                messagesList = messages
                notifyDataSetChanged()
            }
        }
    }

    private fun observeFirestoreChanges(): ListenerRegistration {
        val collRef = Firebase.firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(room.id!!)
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
                        toggleRecyclerView(true)
                        showAddedMessages(mutableListOf(doc.document.toObject(Message::class.java)))
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initRecyclerView() {
        val adapter = MessagesAdapter(mutableListOf())
        binding.messagesRecycler.adapter = adapter
        loadMessages()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadMessages() {
        room.let {
            (binding.messagesRecycler.adapter as MessagesAdapter).apply {
                sharedViewModel.getMessages(room.id!!)
            }
        }
    }

    fun sendMessage() {
        room.id?.let {
            val message = Message(
                binding.messageInput.text.toString(),
                FirebaseAuth.getCurrentAuthUser()?.uid,
                sharedViewModel.userProfile.value?.username,
                it,
                Timestamp.now()
            )
            sharedViewModel.sendMessage(message)
            binding.messageInput.setText("")
        }
    }

    private fun showAddedMessages(newMessages: MutableList<Message>) {
        (binding.messagesRecycler.adapter as MessagesAdapter).apply {
            messagesList?.let {
                it.addAll(newMessages)
                val startPosition = it.size - newMessages.size
                notifyItemRangeInserted(startPosition, newMessages.size)
                binding.messagesRecycler.smoothScrollToPosition(it.size - 1)
            }
        }
    }

    private fun toggleRecyclerView(show: Boolean) {
        binding.messagesRecycler.isVisible = show
        binding.alternativeTextview.isVisible = !show
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        messagesListenerRegistration.remove() // stop observing messages real-time updates
    }
}
package com.mis.route.chatapp.ui.fragments.createroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mis.route.chatapp.R
import com.mis.route.chatapp.data.firebase.RoomCreationState
import com.mis.route.chatapp.data.firebase.RoomCreationStatus
import com.mis.route.chatapp.databinding.FragmentCreateRoomBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.Extensions.buildProgressDialog
import com.mis.route.chatapp.ui.Extensions.showMessage
import com.mis.route.chatapp.ui.DialogMessage
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.fragments.createroom.model.RoomCategories
import com.mis.route.chatapp.ui.model.RoomCategory

/**
 * A simple [Fragment] subclass.
 */
class CreateRoomFragment : Fragment() {
    private var _binding: FragmentCreateRoomBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            createRoomFragment = this@CreateRoomFragment
        }
        initSpinner()
        defineObservationActions()
    }

    private fun handleRoomCreation(roomCreationStatus: RoomCreationStatus) {
        val progressDialogBuilder = buildProgressDialog(
            DialogMessage(
                "Creating a Room",
                "Please, wait until we create your room...",
                cancelable = false
            )
        )
        when (roomCreationStatus.state) {
            RoomCreationState.Succeeded -> {
                progressDialogBuilder.dismiss()
                showMessage(
                    DialogMessage(
                        "Room Created Successfully!",
                        "A new Room has been created for you.",
                        "Ok",
                        { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            navigateToHome()
                        }
                    )
                )
            }

            RoomCreationState.Failed -> {
                progressDialogBuilder.dismiss()
                showMessage(
                    DialogMessage(
                        "Something Went Wrong",
                        content = roomCreationStatus.error,
                        "Ok",
                        { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                    )
                )
            }

            RoomCreationState.Loading -> {
                progressDialogBuilder.show()
            }

            else -> {}
        }
    }

    private fun defineObservationActions() {
        sharedViewModel.roomCreationStatus.observe(viewLifecycleOwner, ::handleRoomCreation)
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_createRoomFragment_to_homeFragment)
    }

    private fun initSpinner() {
        val adapter = RoomCategorySpinnerAdapter(RoomCategories.getCategories())
        binding.roomCategorySpinner.adapter = adapter
    }

    private fun isInputValid(): Boolean {
        var valid = true
        if (binding.roomNameInput.text.isNullOrBlank()) {
            binding.roomNameContainer.error = "Name can't be empty"
            valid = false
        } else {
            binding.roomNameContainer.error = null
        }
        if (binding.roomDescInput.text.isNullOrBlank()) {
            binding.roomDescContainer.error = "Name can't be empty"
            valid = false
        } else {
            binding.roomDescContainer.error = null
        }

        return valid
    }

    fun createRoom() {
        if (!isInputValid()) return
        val room = Room(
            ownerId = sharedViewModel.getCurrentUser()?.uid,
            title = binding.roomNameInput.text.toString(),
            description = binding.roomDescInput.text.toString(),
            category = (binding.roomCategorySpinner.selectedItem as RoomCategory).title,
        )
        sharedViewModel.createRoom(room)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
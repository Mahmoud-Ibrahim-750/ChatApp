package com.mis.route.chatapp.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mis.route.chatapp.R
import com.mis.route.chatapp.data.firebase.AuthState
import com.mis.route.chatapp.data.firebase.LoginState
import com.mis.route.chatapp.databinding.FragmentRegisterBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.Extensions.buildProgressDialog
import com.mis.route.chatapp.ui.Extensions.showMessage
import com.mis.route.chatapp.ui.Message
import com.mis.route.chatapp.ui.UiConstants

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            registerFragment = this@RegisterFragment
        }
        observeProperties()
    }

    private fun observeProperties() {
        val loadingProgressDialog = buildProgressDialog(
            Message(
            title = "Loading",
            content = "Creating your account...",
            cancelable = false
        )
        )
        sharedViewModel.authStatus.observe(viewLifecycleOwner) {
            when (it.state) {
                AuthState.Loading -> {
                    loadingProgressDialog.show()
                }
                AuthState.Succeeded -> {
                    loadingProgressDialog.dismiss()
                    val args = Bundle()
                    args.putString(UiConstants.PASSED_EMAIL, binding.emailInput.text.toString())
                    args.putString(UiConstants.PASSED_PASSWORD, binding.passwordInput.text.toString())
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment, args)
                }
                AuthState.Failed -> {
                    loadingProgressDialog.dismiss()
                    showMessage(Message(
                        title = "Something went wrong",
                        content = it.error,
                        posMessage = "Try again",
                        posAction = { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            registerAccount()
                        },
                        negMessage = "Cancel",
                        negAction = { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        }
                    ))
                }

                else -> {}
            }
        }
    }

    fun registerAccount() {
        if (validateCredentials()) {
            sharedViewModel.createAccount(
                binding.emailInput.text.toString().trim(),
                binding.passwordInput.text.toString().trim()
            )
        }
    }

    private fun validateCredentials() : Boolean {
        var isValid = true
        if (binding.passwordInput.text.isNullOrBlank()) {
            binding.passwordInput.error = "cannot be blank"
            isValid = false
        } else binding.passwordInput.error = null

        if (binding.passwordConfirmInput.text.isNullOrBlank()) {
            isValid = false
        }

        if (binding.passwordInput.text == binding.passwordConfirmInput.text) {
            isValid = false
        }

        return isValid
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.mis.route.chatapp.ui.fragments.register

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mis.route.chatapp.R
import com.mis.route.chatapp.data.firebase.model.auth.AuthState
import com.mis.route.chatapp.data.firebase.model.auth.AuthStatus
import com.mis.route.chatapp.databinding.FragmentRegisterBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.Extensions.showMessage
import com.mis.route.chatapp.ui.UiConstants
import com.mis.route.chatapp.ui.model.DialogMessage

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
        sharedViewModel.authStatus.observe(viewLifecycleOwner, ::handleRegisterEvents)
    }

    private fun handleRegisterEvents(authStatus: AuthStatus) {
        when (authStatus.state) {
            AuthState.Loading -> showLoading(true)

            AuthState.Succeeded -> {
                showLoading(false)
                val args = Bundle().apply {
                    putString(UiConstants.PASSED_EMAIL, binding.emailInput.text.toString())
                    putString(UiConstants.PASSED_PASSWORD, binding.passwordInput.text.toString())
                }
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment, args)
            }
            AuthState.Failed -> {
                showLoading(false)
                showMessage(DialogMessage(
                    title = "Something went wrong",
                    content = authStatus.error,
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

    private fun showLoading(show: Boolean) {
        binding.loadingProgressBar.isVisible = show
        binding.arrowImage.isVisible = !show
    }

    fun registerAccount() {
        if (validateCredentials()) {
            sharedViewModel.createAccount(
                binding.usernameInput.text.toString().trim(),
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
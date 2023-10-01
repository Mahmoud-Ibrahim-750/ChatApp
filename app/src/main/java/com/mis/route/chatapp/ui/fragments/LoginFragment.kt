package com.mis.route.chatapp.ui.fragments

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
import com.mis.route.chatapp.data.firebase.LoginState
import com.mis.route.chatapp.data.firebase.LoginStatus
import com.mis.route.chatapp.databinding.FragmentLoginBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.Extensions.showMessage
import com.mis.route.chatapp.ui.DialogMessage
import com.mis.route.chatapp.ui.UiConstants

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            loginFragment = this@LoginFragment
        }
        arguments?.let { bindCredentials(it) }
        observeProperties()
    }

    private fun bindCredentials(args: Bundle) {
        args.getString(UiConstants.PASSED_EMAIL)?.let {
            binding.emailInput.setText(it)
        }
        args.getString(UiConstants.PASSED_PASSWORD)?.let {
            binding.passwordInput.setText(it)
        }
    }

    private fun observeProperties() {
        sharedViewModel.loginStatus.observe(viewLifecycleOwner, ::handleLoginEvents)
    }

    private fun handleLoginEvents(loginStatus: LoginStatus) {
        when (loginStatus.state) {
            LoginState.Loading -> showLoading(true)

            LoginState.Succeeded -> {
                showLoading(false)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }

            LoginState.Failed -> {
                showLoading(false)
                showMessage(DialogMessage(
                    title = "Something went wrong",
                    content = loginStatus.error,
                    posMessage = "Try again",
                    posAction = { dialogInterface: DialogInterface, _: Int ->
                        login()
                        dialogInterface.dismiss()
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

    fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun login() {
        sharedViewModel.singIn(
            binding.emailInput.text.toString().trim(),
            binding.passwordInput.text.toString().trim()
        )
    }

    fun fakeLogin() {
        sharedViewModel.singIn("aya@dev.com", "123456")
    }

    fun resetPassword() {
        // TODO: implement later
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.mis.route.chatapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mis.route.chatapp.R
import com.mis.route.chatapp.databinding.FragmentSplashBinding
import com.mis.route.chatapp.ui.UiConstants

/**
 * A simple [Fragment] subclass.
 */
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            val args = Bundle()
            args.putString(UiConstants.PASSED_EMAIL, null)
            args.putString(UiConstants.PASSED_PASSWORD, null)
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment, args)
                                                    }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
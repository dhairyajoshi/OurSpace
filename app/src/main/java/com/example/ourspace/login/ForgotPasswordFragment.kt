package com.example.ourspace.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ourspace.R
import com.example.ourspace.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        binding.progressBar.visibility = View.GONE
        binding.savePassword.visibility = View.VISIBLE

        binding.savePassword.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.savePassword.visibility = View.GONE
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
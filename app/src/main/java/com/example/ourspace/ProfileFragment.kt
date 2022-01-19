package com.example.ourspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ourspace.databinding.FragmentProfileBinding
import com.example.ourspace.models.User


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? =null
    private val binding get() = _binding!!
    private val args2 by navArgs<ProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater,container,false)

        val name = binding.userName.text.toString()
        val bio = binding.userBio.text.toString()
        val dob = binding.dob.text.toString()
        val gender = binding.Gender.text.toString()
        val residence = binding.Residence.text.toString()

        val user = User(name,bio,dob,gender,residence)

        binding.editProfile.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(user)
            findNavController().navigate(action)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
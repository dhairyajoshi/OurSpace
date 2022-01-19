package com.example.ourspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ourspace.databinding.FragmentEditProfileBinding
import com.example.ourspace.models.User

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? =null
    private val binding get() = _binding!!
    private val args by navArgs<EditProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater,container,false)

        binding.NameEdit.setText(args.currentUser.name)
        binding.BioEdit.setText(args.currentUser.bio)
        binding.dobEdit.setText(args.currentUser.dob)
        binding.GenderEdit.setText(args.currentUser.gender)
        binding.ResidenceEdit.setText(args.currentUser.residence)

        val name = binding.NameEdit.text.toString()
        val bio = binding.NameEdit.text.toString()
        val dob = binding.NameEdit.text.toString()
        val gender = binding.NameEdit.text.toString()
        val residence = binding.NameEdit.text.toString()

        val editUser = User(name,bio,dob,gender,residence)

        binding.saveButton.setOnClickListener {
            val actionEdit = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment(editUser)
            findNavController().navigate(actionEdit)
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.profileFragment)
                }
            })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
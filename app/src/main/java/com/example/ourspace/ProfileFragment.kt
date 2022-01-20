package com.example.ourspace

import android.app.AlertDialog
import android.os.Bundle
import android.text.method.Touch.onTouchEvent
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ourspace.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val setProfile = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.profilePhoto.setImageURI(uri)
            }
        )
        val setCover = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.coverPhoto.setImageURI(uri)
            }
        )
        binding.setCoverPhoto.setOnClickListener {
            setCover.launch("image/*")
        }
        binding.setProfilePhoto.setOnClickListener {
            setProfile.launch("image/*")
        }

        binding.NameID.setOnClickListener {
            editDetailsDialog("Enter Name")
        }
        binding.bioID.setOnClickListener {
            editDetailsDialog("Enter Bio")
        }

        /*binding.coverPhoto.setOnTouchListener(View.OnTouchListener(
            val gestureDetector = GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Toast.makeText(activity, "Double Tapped", Toast.LENGTH_SHORT).show()
                return true
            }
        })),
            )

        binding.coverPhoto.setOnTouchListener { v, event ->
            return@setOnTouchListener  gestureDetector.onTouchEvent(event)
        }*/

        return binding.root
    }

    private fun editDetailsDialog(title: String) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogLayout = requireActivity().layoutInflater.inflate(R.layout.edit_text_dialog, null)
        val editDetails = dialogLayout.findViewById<EditText>(R.id.editDetails)
        with(builder) {
            setTitle(title)
            setPositiveButton("Save") { _, _ ->
                when (title) {
                    "Enter Name" -> {
                        binding.Name.text = editDetails.text.toString()
                    }
                    "Enter Bio" -> {
                        binding.bio.text = editDetails.text.toString()
                    }
                }
            }
            setNegativeButton("Discard") { _, _ ->
                Log.d("Main", "Negative Button Clicked")
            }
            setView(dialogLayout)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
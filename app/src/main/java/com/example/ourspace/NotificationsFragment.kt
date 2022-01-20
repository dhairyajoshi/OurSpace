package com.example.ourspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ourspace.adapters.NotificationsRVAdapter
import com.example.ourspace.databinding.FragmentNotificationsBinding
import com.example.ourspace.models.NewNotifications


class NotificationsFragment : Fragment() {


    private lateinit var newArrayList: ArrayList<NewNotifications>
    private lateinit var userimg: Array<Int>
    private lateinit var username: Array<String>
    private lateinit var date: Array<String>
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val greetingArgs: NotificationsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        userimg = arrayOf(
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars,
            R.drawable.ic_avatars

        )
        username = arrayOf(
            "Adarsh Goswami liked your post.",
            "Rahul Sahoo liked your post.",
            "Riya Singh liked your post.",
            "Adarsh Goswami liked your post.",
            "Adarsh Goswami liked your post.",
            "Adarsh Goswami liked your post.",
            "Adarsh Goswami liked your post.",
            "Adarsh Goswami liked your post.",
            "Adarsh Goswami liked your post.",
            "Adarsh Goswami liked your post."
        )
        date = arrayOf(
            "Just Now",
            "few moments ago",
            "an hour ago",
            "3h ago",
            "Yesterday",
            "Yesterday",
            "Yesterday",
            "Yesterday",
            "Yesterday",
            "Yesterday"
        )
        newArrayList = arrayListOf()
        getUserdata()
        binding.greetings.text = greetingArgs.greetingMessage

        binding.notificationsRV.adapter = NotificationsRVAdapter(newArrayList)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_notificationsFragment_to_feedFragment)
                }
            })

        return binding.root
    }

    private fun getUserdata() {

        for (i in username.indices) {
            val notifications = NewNotifications(userimg[i], username[i], date[i])
            newArrayList.add(notifications)
        }
    }

}
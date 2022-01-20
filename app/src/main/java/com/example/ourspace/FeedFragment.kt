package com.example.ourspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ourspace.adapters.FeedRVAdapter
import com.example.ourspace.databinding.FragmentFeedBinding
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import java.util.*


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        val userNames = resources.getStringArray(R.array.userNames)
        val uploadTimes = resources.getStringArray(R.array.uploadTimes)
        val captions = resources.getStringArray(R.array.captions)

        val adapter = FeedRVAdapter(userNames, uploadTimes, captions)
        binding.feedRV.adapter = adapter

        val greeting = getGreetingMessage() + "Adarsh"
        binding.greetings.text = greeting

        binding.notifications.setOnClickListener {
            val greetingMessagePass = FeedFragmentDirections.actionFeedFragmentToNotificationsFragment(greeting)
            findNavController().navigate(greetingMessagePass)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getGreetingMessage(): String {
        val c = Calendar.getInstance()

        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning, "
            in 12..15 -> "Good Afternoon, "
            in 16..20 -> "Good Evening, "
            in 21..23 -> "Good Night, "
            else -> "Hello, "
        }
    }
}
package com.example.ourspace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ourspace.adapters.FeedRVAdapter
import com.example.ourspace.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater,container,false)

        val userNames = resources.getStringArray(R.array.userNames)
        val uploadTimes = resources.getStringArray(R.array.uploadTimes)
        val captions = resources.getStringArray(R.array.captions)

        val adapter = FeedRVAdapter(userNames, uploadTimes, captions)
        binding.feedRV.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
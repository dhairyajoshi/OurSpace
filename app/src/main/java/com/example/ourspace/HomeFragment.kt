package com.example.ourspace

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.example.ourspace.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var bottomNavSelectedItemId = R.id.feed
    private val bottomNavSelectedItemIdKey = "BOTTOM_NAV_SELECTED_ITEM_ID_KEY"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.create.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createFragment)
        }

        binding.logout.setOnClickListener {
            var shredpref= this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
            var editor= shredpref.edit()
            editor.apply{
                putString("token",null)
                putBoolean("isLogin",false)
                apply()
            }
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            bottomNavSelectedItemId =
                savedInstanceState.getInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        }
        setupBottomNavBar()
    }

    // Needed to maintain correct state over rotations
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(bottomNavSelectedItemIdKey, bottomNavSelectedItemId)
        super.onSaveInstanceState(outState)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupBottomNavBar() {
        // Your navGraphIds must have the same ids as your menuItem ids
        val navGraphIds = listOf(R.navigation.feed, R.navigation.profile)

        binding.bottomNav.selectedItemId = bottomNavSelectedItemId // Needed to maintain correct state on return

        val controller = binding.bottomNav.setupWithNavController(
            fragmentManager = childFragmentManager,
            navGraphIds = navGraphIds,
            backButtonBehaviour = BackButtonBehaviour.SHOW_STARTING_FRAGMENT,
            containerId = R.id.fragmentHomeContainer,
            firstItemId = R.id.feed, // Must be the same as bottomNavSelectedItemId
            intent = requireActivity().intent
        )

        binding.bottomNav.menu.getItem(1).isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.ourspace

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ourspace.adapters.NotificationsRVAdapter
import com.example.ourspace.databinding.FragmentNotificationsBinding
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.NotificationResponse
import com.example.ourspace.retrofit.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationsFragment : Fragment() {


    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var swipeContainer: SwipeRefreshLayout? = null
    private val greetingArgs: NotificationsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        binding.shimmer.startShimmer()
        binding.notificationsRV.visibility = View.GONE

        val shredpref =
            this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        val editor = shredpref.edit()
        val token: String = shredpref.getString("token", null).toString()
        val header = "Bearer $token"

        val userResponse = ApiClient.userService.getUser(header)

        userResponse.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if (response.isSuccessful) {
                    val fullName = response.body()?.first_name
                    val parts = fullName?.split(" ")?.toMutableList()
                    val firstName = parts?.firstOrNull()
                    binding.greetings.text = "${firstName}'s notifications"
                } else {

                    editor.apply {
                        putString("token", null)
                        putBoolean("isLogin", false)
                        apply()
                    }
                    Toast.makeText(
                        context,
                        "Couldn't fetch data, please login again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Couldn't fetch data, please login again",
                    Toast.LENGTH_SHORT
                ).show()
                editor.apply {
                    putString("token", null)
                    putBoolean("isLogin", false)
                    apply()
                }

            }
        })

        val notificationResponse = ApiClient.userService.getNotifs(header)
        notificationResponse.enqueue(object : Callback<List<NotificationResponse>?> {
            override fun onResponse(
                call: Call<List<NotificationResponse>?>,
                response: Response<List<NotificationResponse>?>
            ) {
                if (response.isSuccessful) {
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.notificationsRV.visibility = View.VISIBLE
                    val adapter = context?.let {
                        response.body()?.let { it1 ->
                            NotificationsRVAdapter(
                                it,
                                it1
                            )
                        }
                    }
                    binding.notificationsRV.adapter = adapter
                } else {
                    editor.apply {
                        putString("token", null)
                        putBoolean("isLogin", false)
                        apply()
                    }
                    Toast.makeText(
                        context,
                        "Couldn't fetch data, please login again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<NotificationResponse>?>, t: Throwable) {
                editor.apply {
                    putString("token", null)
                    putBoolean("isLogin", false)
                    apply()
                }
                Toast.makeText(
                    context,
                    "Couldn't fetch data, please login again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })





        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_notificationsFragment_to_feedFragment)
                }
            })

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        swipeContainer = binding.swipeContainer
        // Setup refresh listener which triggers new data loading
        swipeContainer!!.setOnRefreshListener {
            binding.shimmer.startShimmer()
            binding.notificationsRV.visibility = View.GONE
            val shredpref =
                this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
            val editor = shredpref.edit()
            val token: String = shredpref.getString("token", null).toString()
            val header = "Bearer $token"

            val notificationResponse = ApiClient.userService.getNotifs(header)
            notificationResponse.enqueue(object : Callback<List<NotificationResponse>?> {
                override fun onResponse(
                    call: Call<List<NotificationResponse>?>,
                    response: Response<List<NotificationResponse>?>
                ) {
                    if (response.isSuccessful) {

                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.notificationsRV.visibility = View.VISIBLE

                        val adapter = context?.let {
                            response.body()?.let { it1 ->
                                NotificationsRVAdapter(
                                    it,
                                    it1
                                )
                            }
                        }
                        binding.notificationsRV.adapter = adapter
                        swipeContainer!!.isRefreshing = false
                    } else {
                        editor.apply {
                            putString("token", null)
                            putBoolean("isLogin", false)
                            apply()
                        }
                        Toast.makeText(
                            context,
                            "Couldn't fetch data, please login again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<NotificationResponse>?>, t: Throwable) {
                    editor.apply {
                        putString("token", null)
                        putBoolean("isLogin", false)
                        apply()
                    }
                    Toast.makeText(
                        context,
                        "Couldn't fetch data, please login again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
        // Configure the refreshing colors
        swipeContainer!!.setColorSchemeResources(
            R.color.loaderblue,
            R.color.loaderpink,
            R.color.loader_black,
        )
    }


}
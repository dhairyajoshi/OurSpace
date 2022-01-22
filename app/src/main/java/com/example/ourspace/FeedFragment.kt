package com.example.ourspace

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ourspace.adapters.FeedRVAdapter
import com.example.ourspace.databinding.FragmentFeedBinding
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.PostResponse
import com.example.ourspace.retrofit.UserResponse
import retrofit2.Call
import retrofit2.Response
import java.util.*


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private var swipeContainer: SwipeRefreshLayout? = null
    private val binding get() = _binding!!
    lateinit var adapter: FeedRVAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        binding.shimmer.startShimmer()
        binding.feedRV.visibility = View.GONE

        binding.notifications.setOnClickListener {
            findNavController().navigate(R.id.notificationsFragment)
        }

        return binding.root

    }

    override fun onResume() {
        super.onResume()

        swipeContainer = binding.swipeContainer
        // Setup refresh listener which triggers new data loading
        swipeContainer!!.setOnRefreshListener {
            binding.shimmer.startShimmer()
            binding.shimmer.visibility = View.VISIBLE
            binding.feedRV.visibility = View.GONE
            val shredpref =
                this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
            val editor = shredpref.edit()
            val token: String = shredpref.getString("token", null).toString()
            val header = "Bearer $token"
            val postResponse = ApiClient.userService.getPosts(header)

            postResponse.enqueue(object : retrofit2.Callback<List<PostResponse>?> {
                override fun onResponse(
                    call: Call<List<PostResponse>?>,
                    response: Response<List<PostResponse>?>
                ) {
                    if (response.isSuccessful) {

                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.feedRV.visibility = View.VISIBLE

                        adapter =
                            activity?.let {
                                response.body()?.let { it1 -> FeedRVAdapter(it, it1) }
                            }!!
                        binding.feedRV.adapter = adapter
                        swipeContainer!!.isRefreshing = false
                    } else {
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
                }

                override fun onFailure(call: Call<List<PostResponse>?>, t: Throwable) {
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

        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            if (activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } == true) {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
                    )
                }
            } else {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
                    )
                }
            }
        }


        val shredpref =
            this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        val editor = shredpref.edit()
        val token: String = shredpref.getString("token", null).toString()
        val header = "Bearer $token"
        val postResponse = ApiClient.userService.getPosts(header)
        val userResponse = ApiClient.userService.getUser(header)

        userResponse.enqueue(object : retrofit2.Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if (response.isSuccessful) {
                    binding.greetings.visibility = View.VISIBLE
                    val fullName = response.body()?.first_name
                    val parts = fullName?.split(" ")?.toMutableList()
                    val firstName = parts?.firstOrNull()
                    binding.greetings.text = "${getGreetingMessage()}${firstName}"
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

        postResponse.enqueue(object : retrofit2.Callback<List<PostResponse>?> {
            override fun onResponse(
                call: Call<List<PostResponse>?>,
                response: Response<List<PostResponse>?>
            ) {
                if (response.isSuccessful) {

                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.feedRV.visibility = View.VISIBLE
                    adapter =
                        activity?.let { response.body()?.let { it1 -> FeedRVAdapter(it, it1) } }!!
                    binding.feedRV.adapter = adapter
                } else {
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
            }

            override fun onFailure(call: Call<List<PostResponse>?>, t: Throwable) {
                Toast.makeText(context, "Please login again", Toast.LENGTH_SHORT).show()
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((activity?.let {
                            ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        } ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
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
            in 16..23 -> "Good Evening, "

            else -> "Hello, "
        }
    }
}
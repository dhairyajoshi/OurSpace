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
import com.bumptech.glide.Glide
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
    private val greetingArgs: NotificationsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        var shredpref= this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        var editor= shredpref.edit()
        var token: String = shredpref.getString("token",null).toString()
        var header= "Bearer $token"

        var userResponse = ApiClient.userService.getUser(header)

        userResponse.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if (response.isSuccessful)
                {
                    binding.greetings.text = "${response.body()?.first_name}'s notifications"
                }
                else{

                    editor.apply{
                        putString("token",null)
                        putBoolean("isLogin",false)
                        apply()
                    }
                    Toast.makeText(context, "Couldn't fetch data, please login again", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Toast.makeText(context, "Couldn't fetch data, please login again", Toast.LENGTH_SHORT).show()
                editor.apply{
                    putString("token",null)
                    putBoolean("isLogin",false)
                    apply()
                }

            }
        })

        var notificationResponse= ApiClient.userService.getNotifs(header)
        notificationResponse.enqueue(object : Callback<List<NotificationResponse>?> {
            override fun onResponse(
                call: Call<List<NotificationResponse>?>,
                response: Response<List<NotificationResponse>?>
            ) {
                if(response.isSuccessful )
                {
                    var adapter= context?.let { response.body()?.let { it1 ->
                        NotificationsRVAdapter(it,
                            it1
                        )
                    } }
                    binding.notificationsRV.adapter=adapter
                }
                else{
                    editor.apply{
                        putString("token",null)
                        putBoolean("isLogin",false)
                        apply()
                    }
                    Toast.makeText(context, "Couldn't fetch data, please login again", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NotificationResponse>?>, t: Throwable) {
                editor.apply{
                    putString("token",null)
                    putBoolean("isLogin",false)
                    apply()
                }
                Toast.makeText(context, "Couldn't fetch data, please login again", Toast.LENGTH_SHORT).show()
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



}
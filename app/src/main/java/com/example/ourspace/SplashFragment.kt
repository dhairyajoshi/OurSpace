package com.example.ourspace

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler(Looper.getMainLooper()).postDelayed({
            val shredpref= this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
            var editor=shredpref.edit()
            val token: String = shredpref.getString("token",null).toString()
            if(token==null)
            {
                findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
            }
            val header= "Bearer $token"

            val loginResponse = ApiClient.userService.getUser(header)

            loginResponse.enqueue(object : Callback<UserResponse?> {
                override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                    if (response.isSuccessful)
                    {
                        findNavController().navigate(R.id.homeFragment)
                    }
                    else
                    {
                        findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                    }
                }

                override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                    findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                }
            })

        },500)

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}
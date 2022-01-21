package com.example.ourspace

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ourspace.databinding.FragmentUserProfileBinding
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileFragment : Fragment() {

    private var _binding:FragmentUserProfileBinding?=null
    private val binding get() = _binding!!
    lateinit var usrname:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserProfileBinding.inflate(inflater,container,false)

        usrname= arguments?.getString("username")!!

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_userProfileFragment_to_feedFragment)
                }
            })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        var shredpref= this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        var editor= shredpref.edit()
        var token: String = shredpref.getString("token",null).toString()
        var header= "Bearer $token"
        var userResponse = ApiClient.userService.getUserPrf(header,usr = usrname)

        userResponse.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if(response.isSuccessful)
                {
                    binding.userName.text=response.body()?.username.toString()
                    binding.Name.text=response.body()?.first_name.toString()
                    binding.noOfLikes.text=response.body()?.likes.toString()
                    binding.noOfPosts.text=response.body()?.posts.toString()

                    // shimmers here

                    Glide.with(activity!!)
                        .load("${ApiClient.BASE_URL}${response.body()?.pfp}")
                        .placeholder(R.drawable.ic_logo)
                        .into(binding.profilePhoto);

                    Glide.with(activity!!)
                        .load("${ApiClient.BASE_URL}${response.body()?.cfp}")
                        .placeholder(R.drawable.ic_logo)
                        .into(binding.coverPhoto);
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
                editor.apply{
                    putString("token",null)
                    putBoolean("isLogin",false)
                    apply()

                }
                Toast.makeText(context, "Couldn't fetch data, please login again", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
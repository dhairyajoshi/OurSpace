package com.example.ourspace.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ourspace.R
import com.example.ourspace.databinding.FragmentRegisterBinding
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.SignupResponse
import com.example.ourspace.retrofit.UserRegister
import com.example.ourspace.retrofit.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.progressBar.visibility = View.GONE
        binding.register.visibility = View.VISIBLE

        var shredpref =
            this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        var editor = shredpref.edit()
        var token: String = shredpref.getString("token", null).toString()
        var header = "Bearer $token"




        binding.register.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            binding.register.visibility = View.GONE

            if (binding.userName.text.isNullOrEmpty()) {
                binding.userName.error = "username cannot be empty!"
                return@setOnClickListener
            }
            if (binding.fullName.text.isNullOrEmpty()) {
                binding.fullName.error = "full name cannot be empty!"
                return@setOnClickListener
            }
            if (binding.Password.text.toString().length < 8) {
                binding.Password.error = "Password must have 8 characters"
                return@setOnClickListener
            }
            if (!binding.Password.text.toString().equals(binding.ConfirmPassword.text.toString())) {
                binding.ConfirmPassword.error = "Both passwords must not be empty and must match!"
                return@setOnClickListener
            }

            var username = binding.userName.text.toString()
            var name = binding.fullName.text.toString()
            var password = binding.Password.text.toString()

            var user = UserRegister(username, name, password)

            var loginResponse = ApiClient.userService.registerUser(user)

            loginResponse.enqueue(object : Callback<SignupResponse?> {
                override fun onResponse(
                    call: Call<SignupResponse?>,
                    response: Response<SignupResponse?>
                ) {
                    if (response.isSuccessful) {
                        editor.apply {
                            if (response.body() != null) {
                                putString("token", response.body()?.token.toString())
                                putBoolean("isLogin", true)
                                apply()
                            }
                        }
                        findNavController().navigate(R.id.homeFragment)
                    } else {
                        Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT)
                            .show()
                        binding.progressBar.visibility = View.GONE
                        binding.register.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<SignupResponse?>, t: Throwable) {
                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.register.visibility = View.VISIBLE
                }
            })

        }
        binding.moveToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

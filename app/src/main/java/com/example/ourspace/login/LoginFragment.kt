package com.example.ourspace.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ourspace.R
import com.example.ourspace.databinding.FragmentLoginBinding
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.LoginResponse
import com.example.ourspace.retrofit.UserLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.GONE
        binding.login.visibility = View.VISIBLE
        val shredpref =
            this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        val editor = shredpref.edit()
        binding.login.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            binding.login.visibility = View.GONE
            if (binding.UserName.text != null && binding.Password.text != null) {
                val username = binding.UserName.text.toString().trim()
                val password = binding.Password.text.toString().trim()

                val user = UserLogin(username, password)

                Log.d("login", user.toString())

                val loginResponse = ApiClient.userService.loginUser(user)

                loginResponse.enqueue(object : Callback<LoginResponse?> {
                    override fun onResponse(
                        call: Call<LoginResponse?>,
                        response: Response<LoginResponse?>
                    ) {
                        if (response.isSuccessful && response.body()?.token != null) {
                            Log.d("login", response.body().toString())
                            editor.apply {
                                putString("token", response.body()?.token.toString())
                                putBoolean("isLogin", true)
                                apply()
                            }

                            findNavController().navigate(R.id.homeFragment)
                        } else {
                            binding.progressBar.visibility = View.GONE
                            binding.login.visibility = View.VISIBLE
                            Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }

                    override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                        binding.progressBar.visibility = View.GONE
                        binding.login.visibility = View.VISIBLE
                        Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            }
        }
        binding.moveToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.ourspace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ourspace.databinding.FragmentCreateBinding
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.LikeResponse
import com.example.ourspace.retrofit.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    var reqFile: RequestBody? = null
    var body: MultipartBody.Part? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateBinding.inflate(inflater, container, false)

        binding.progressBar.visibility = View.GONE
        binding.publish.visibility = View.VISIBLE

        /*val addImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.postImage.setImageURI(uri)
            }
        )*/

        binding.addImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, MainActivity.PICK_IMG)
        }

        binding.discard.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        binding.publish.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            binding.publish.visibility = View.GONE

            if (binding.caption.text.isNullOrEmpty()) {
                Toast.makeText(context, "Please provide a caption", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (body == null) {
                Toast.makeText(context, "Please upload a photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val shredpref =
                this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
            val token: String = shredpref.getString("token", null).toString()
            val header = "Bearer $token"
            val cap = binding.caption.text.toString()
            val data = MultipartBody.Part.createFormData("caption", cap)

            val uploadResponse =
                ApiClient.userService.addPost(token = header, image = body!!, cap = data)

            uploadResponse.enqueue(object : Callback<LikeResponse?> {
                override fun onResponse(
                    call: Call<LikeResponse?>,
                    response: Response<LikeResponse?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "${response.body()?.msg}", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.action_createFragment_to_homeFragment)
                    } else {

                        Toast.makeText(context, "${response.body()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LikeResponse?>, t: Throwable) {
                    Log.d("upload", t.toString())
                    Toast.makeText(context, "$t", Toast.LENGTH_SHORT).show()
                }
            })

        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.homeFragment)
                }
            })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val shredpref =
            this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        val editor = shredpref.edit()
        val token: String = shredpref.getString("token", null).toString()
        val header = "Bearer $token"

        val userResponse = ApiClient.userService.getUser(header)

        userResponse.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if (response.isSuccessful) {
                    Glide.with(activity!!)
                        .load("${ApiClient.BASE_URL}${response.body()?.pfp}")
                        .circleCrop()
                        .placeholder(R.drawable.ic_avatars)
                        .into(binding.usepfp)

                } else {
                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
                    editor.apply {
                        putString("token", null)
                        putBoolean("isLogin", false)
                        apply()
                    }
                    findNavController().navigate(R.id.loginFragment)
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
                editor.apply {
                    putString("token", null)
                    putBoolean("isLogin", false)
                    apply()
                }
                findNavController().navigate(R.id.loginFragment)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.PICK_IMG && resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImage = data!!.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                requireActivity().contentResolver.query(
                    selectedImage!!,
                    filePathColumn,
                    null,
                    null,
                    null
                ) ?: return
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            val file = File(filePath)
            reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            body = MultipartBody.Part.createFormData("pic", file.name, reqFile!!)
            binding.postImage.setImageURI(data.data)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
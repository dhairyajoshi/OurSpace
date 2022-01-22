package com.example.ourspace

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.Touch.onTouchEvent
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ourspace.MainActivity.Companion.PICK_CFP
import com.example.ourspace.MainActivity.Companion.PICK_IMAGE
import com.example.ourspace.databinding.FragmentProfileBinding
import com.example.ourspace.retrofit.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import java.io.File


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.coverShimmer.startShimmer()
        binding.coverShimmer.visibility = View.VISIBLE
        binding.updateCoverShimmer.visibility = View.GONE
        binding.updateProfileShimmer.visibility = View.GONE


        val setProfile = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.profilePhoto.setImageURI(uri)
            }
        )
        val setCover = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.coverPhoto.setImageURI(uri)
            }
        )
        binding.setCoverPhoto.setOnClickListener {
            binding.updateCoverShimmer.startShimmer()
            binding.updateCoverShimmer.visibility = View.VISIBLE
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_CFP)
        }
        binding.setProfilePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        binding.NameID.setOnClickListener {
            editDetailsDialog("Enter Name")
        }
        binding.bioID.setOnClickListener {
            editDetailsDialog("Enter Bio")
        }


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
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
            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("pfp", file.name, reqFile)
            var shredpref =
                this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
            var editor = shredpref.edit()
            var token: String = shredpref.getString("token", null).toString()
            var header = "Bearer $token"


            var uploadResponse = ApiClient.userService.updatePfp(header, body)
            uploadResponse.enqueue(object : Callback<UserResponse?> {
                override fun onResponse(
                    call: Call<UserResponse?>,
                    response: Response<UserResponse?>
                ) {
                    if (response.isSuccessful) {

                        context?.let {
                            Glide.with(it)
                                .load("${ApiClient.BASE_URL}${response.body()?.pfp}")
                                .placeholder(R.drawable.ic_logo)
                                .circleCrop()
                                .into(binding.profilePhoto)

                            // <-------------profile shimmers here--------------->
                            binding.updateProfileShimmer.stopShimmer()
                            binding.updateProfileShimmer.visibility = View.GONE
                        };
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
        } else if (requestCode == PICK_CFP && resultCode == AppCompatActivity.RESULT_OK) {
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
            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("cfp", file.name, reqFile)
            var shredpref =
                this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
            var editor = shredpref.edit()
            var token: String = shredpref.getString("token", null).toString()
            var header = "Bearer $token"


            var uploadResponse = ApiClient.userService.updateCfp(header, body)
            uploadResponse.enqueue(object : Callback<UserResponse?> {
                override fun onResponse(
                    call: Call<UserResponse?>,
                    response: Response<UserResponse?>
                ) {
                    if (response.isSuccessful) {

                        context?.let {
                            Glide.with(it)
                                .load("${ApiClient.BASE_URL}${response.body()?.cfp}")
                                .placeholder(R.drawable.ic_logo)
                                .into(binding.coverPhoto)
                        };

                        // <------------- cover shimmers here--------------->
                        binding.updateCoverShimmer.stopShimmer()
                        binding.updateCoverShimmer.visibility = View.GONE
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
        }

    }

    override fun onResume() {
        super.onResume()
        var shredpref =
            this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        var editor = shredpref.edit()
        var token: String = shredpref.getString("token", null).toString()
        var header = "Bearer $token"


        var userResponse = ApiClient.userService.getUser(header)

        userResponse.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if (response.isSuccessful) {

                    binding.coverShimmer.stopShimmer()
                    binding.coverShimmer.visibility = View.GONE

                    binding.userName.text = response.body()?.username.toString()
                    binding.Name.text = response.body()?.first_name.toString()
                    binding.bio.text = response.body()?.bio.toString()
                    binding.noOfPosts.text = response.body()?.posts.toString()
                    binding.noOfLikes.text = response.body()?.likes.toString()
                    Glide.with(activity!!)
                        .load("${ApiClient.BASE_URL}${response.body()?.pfp}")
                        .placeholder(R.drawable.ic_logo)
                        .into(binding.profilePhoto);

                    Glide.with(activity!!)
                        .load("${ApiClient.BASE_URL}${response.body()?.cfp}")
                        .placeholder(R.drawable.ic_logo)
                        .into(binding.coverPhoto);

                    // <-------------both shimmers here--------------->
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
    }

    private fun editDetailsDialog(title: String) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogLayout = requireActivity().layoutInflater.inflate(R.layout.edit_text_dialog, null)
        val editDetails = dialogLayout.findViewById<EditText>(R.id.editDetails)
        var shredpref =
            this.requireActivity().getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        var editor = shredpref.edit()
        var token: String = shredpref.getString("token", null).toString()
        var header = "Bearer $token"
        with(builder) {
            setTitle(title)
            setPositiveButton("Save") { _, _ ->
                when (title) {
                    "Enter Name" -> {
                        binding.Name.text = editDetails.text.toString()
                    }
                    "Enter Bio" -> {
                        binding.bio.text = editDetails.text.toString()
                    }
                }
                var user = UserUpdate(binding.Name.text.toString(), binding.bio.text.toString())

                var updateResponse = ApiClient.userService.updateUser(header, user)
                updateResponse.enqueue(object : Callback<LikeResponse?> {
                    override fun onResponse(
                        call: Call<LikeResponse?>,
                        response: Response<LikeResponse?>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "${response.body()?.msg}", Toast.LENGTH_SHORT)
                                .show()
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

                    override fun onFailure(call: Call<LikeResponse?>, t: Throwable) {
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
            setNegativeButton("Discard") { _, _ ->
                Log.d("Main", "Negative Button Clicked")
            }
            setView(dialogLayout)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
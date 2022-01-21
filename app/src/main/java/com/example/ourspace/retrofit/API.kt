package com.example.ourspace.retrofit


import okhttp3.MultipartBody
import retrofit2.http.*
import retrofit2.http.Multipart
interface API {

    @POST("users/login/")
    fun loginUser(@Body userLogin: UserLogin): retrofit2.Call<LoginResponse>

    @POST("users/register/")
    fun registerUser(@Body user: UserRegister): retrofit2.Call<SignupResponse>

    @GET("post/getposts")
    fun getPosts(@Header("Authorization") token:String): retrofit2.Call<List<PostResponse>>

    @POST("post/likepost/{id}")
    fun likePost(@Header("Authorization") token:String,@Path("id") id:Int): retrofit2.Call<LikedResponse>

    @GET("users/getinfo")
    fun getUser(@Header("Authorization") token:String): retrofit2.Call<UserResponse>

    @GET("users/getinfo/{usr}")
    fun getUserPrf(@Header("Authorization") token:String,@Path("usr") usr:String): retrofit2.Call<UserResponse>

    @GET("post/isliked/{id}")
    fun isLiked(@Header("Authorization") token:String,@Path("id") id:Int): retrofit2.Call<LikeResponse>

    @POST("users/updateprofile/")
    fun updateUser(@Header("Authorization") token:String, @Body user:UserUpdate): retrofit2.Call<LikeResponse>

    @GET("users/getnotifs")
    fun getNotifs(@Header("Authorization") token:String): retrofit2.Call<List<NotificationResponse>>

    @Multipart
    @POST("users/updatepfp/")
    fun updatePfp(@Header("Authorization") token:String,@Part image:MultipartBody.Part): retrofit2.Call<UserResponse>

    @Multipart
    @POST("post/addpost")
    fun addPost(@Header("Authorization") token:String, @Part image:MultipartBody.Part, @Part cap: MultipartBody.Part): retrofit2.Call<LikeResponse>

    @Multipart
    @POST("users/updatecfp/")
    fun updateCfp(@Header("Authorization") token:String,@Part image:MultipartBody.Part): retrofit2.Call<UserResponse>


}

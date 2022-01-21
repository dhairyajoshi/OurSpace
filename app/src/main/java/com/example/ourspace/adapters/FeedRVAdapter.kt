package com.example.ourspace.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourspace.FeedFragment
import com.example.ourspace.R
import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.ApiClient.BASE_URL
import com.example.ourspace.retrofit.LikeResponse
import com.example.ourspace.retrofit.LikedResponse
import com.example.ourspace.retrofit.PostResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedRVAdapter(var context: Context, var posts:List<PostResponse>) : RecyclerView.Adapter<FeedRVAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val uploadTime: TextView = view.findViewById(R.id.time)
        val caption: TextView = view.findViewById(R.id.caption)
        val profilePhoto : ImageView = view.findViewById(R.id.profilePhoto)
        val image : ImageView = view.findViewById(R.id.image)
        val like:ImageView = view.findViewById(R.id.like)
        val noOflikes:TextView= view.findViewById(R.id.noOfLikes)

        init{
            itemView.setOnClickListener (object : DoubleClickListener() {
                override fun onDoubleClick(v: View?) {
                    val position= adapterPosition
                    likePost(position,like=like,noOflikes = noOflikes)
                }
            })

            itemView.isSoundEffectsEnabled=false
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_items_layout, parent, false)

        return ItemViewHolder(adapterLayout)    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentUserName = posts[position].username
        val currentUploadTime = posts[position].date
        val currentCaption = posts[position].caption
        holder.userName.text = currentUserName
        holder.uploadTime.text = currentUploadTime
        holder.caption.text = currentCaption
        var shredpref= context.getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        var token: String = shredpref.getString("token",null).toString()
        var header= "Bearer $token"
        var likeResponse= ApiClient.userService.isLiked(header,posts[position].id)

        likeResponse.enqueue(object : Callback<LikeResponse?> {
            override fun onResponse(call: Call<LikeResponse?>, response: Response<LikeResponse?>) {
                if(response.isSuccessful)
                {
                    var res= if (response.body()?.msg.toString()=="1") R.drawable.ic_favorite_fill else R.drawable.ic_favorite_light
                    holder.like.setImageResource(res)
                }
            }

            override fun onFailure(call: Call<LikeResponse?>, t: Throwable) {
                Toast.makeText(context, "something went wrong...", Toast.LENGTH_SHORT).show()
            }
        })

        holder.like.setOnClickListener {likePost(position,holder=holder)}
        holder.like.isSoundEffectsEnabled=false
        holder.noOflikes.text=posts[position].likes.toString()
        holder.image.adjustViewBounds
        Glide.with(context)
            .load("${BASE_URL}${posts[position].pic}")
            .placeholder(R.drawable.ic_logo)
            .into(holder.image);
        Glide.with(context)
            .load("${BASE_URL}${posts[position].pfp}")
            .placeholder(R.drawable.ic_logo)
            .circleCrop()
            .into(holder.profilePhoto);

        val bundle = Bundle()
        bundle.putString("username", posts[position].username)

        holder.userName.setOnClickListener {

            Navigation.createNavigateOnClickListener(R.id.action_feedFragment_to_userProfileFragment,bundle).onClick(holder.userName)
        }

        holder.profilePhoto.setOnClickListener {
            Navigation.createNavigateOnClickListener(R.id.action_feedFragment_to_userProfileFragment,bundle).onClick(holder.profilePhoto)
        }

    }

    override fun getItemCount(): Int {
        return posts.size
    }

    abstract class DoubleClickListener : View.OnClickListener {
        var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
            }
            lastClickTime = clickTime
        }

        abstract fun onDoubleClick(v: View?)

        companion object {
            private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
        }
    }

    fun likePost(position: Int,holder: ItemViewHolder?=null,like:ImageView?=null,noOflikes:TextView?=null) {
        var id = posts[position].id
        var shredpref = context.getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        var token: String = shredpref.getString("token", null).toString()
        var header = "Bearer $token"
        var likeResponse = ApiClient.userService.likePost(header, id)
        likeResponse.enqueue(object : Callback<LikedResponse?> {
            override fun onResponse(
                call: Call<LikedResponse?>,
                response: Response<LikedResponse?>
            ) {
                if (response.isSuccessful) {
                    var res =
                        if (response.body()?.msg.toString() == "1")  R.drawable.ic_favorite_light else  R.drawable.ic_favorite_fill
                    if (like == null) {
                        holder!!.like.setImageResource(res)
                        holder!!.noOflikes.text = response.body()?.count.toString()
                    } else {
                        noOflikes?.text = response.body()?.count.toString()
                        like.setImageResource(res)
                    }
                }
            }

            override fun onFailure(call: Call<LikedResponse?>, t: Throwable) {
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        })

    }

}
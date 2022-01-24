package com.example.ourspace.adapters

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.example.ourspace.R
import com.example.ourspace.models.Utils
import com.example.ourspace.retrofit.*
import com.example.ourspace.retrofit.ApiClient.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.file.Files.delete

class FeedRVAdapter(var context: Context, var posts: List<PostResponse>) :
    RecyclerView.Adapter<FeedRVAdapter.ItemViewHolder>() {

    lateinit var avd: AnimatedVectorDrawableCompat
    lateinit var avd2: AnimatedVectorDrawable

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val uploadTime: TextView = view.findViewById(R.id.time)
        val caption: TextView = view.findViewById(R.id.caption)
        val profilePhoto: ImageView = view.findViewById(R.id.profilePhoto)
        val image: ImageView = view.findViewById(R.id.image)
        val like: ImageView = view.findViewById(R.id.like)
        val menu: ImageView = view.findViewById(R.id.popupMenu)
        val noOflikes: TextView = view.findViewById(R.id.noOfLikes)
        val dpHeart: ImageView = view.findViewById(R.id.dpHeart)
        val drawable: Drawable = dpHeart.drawable

        init {

            itemView.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick(v: View?) {
                    var position= adapterPosition
                    dpHeart.alpha = 0.8f
                    if (drawable is AnimatedVectorDrawableCompat) {
                        avd = drawable
                        avd.start()
                    } else if (drawable is AnimatedVectorDrawable) {
                        avd2 = drawable
                        avd2.start()
                    }
                    val shredpref = context.getSharedPreferences("ourspace", Context.MODE_PRIVATE)
                    val token: String = shredpref.getString("token", null).toString()
                    val header = "Bearer $token"
                    val likeResponse =
                        ApiClient.userService.likePost(header, posts[position].id, "dt")
                    likeResponse.enqueue(object : Callback<LikedResponse?> {
                        override fun onResponse(
                            call: Call<LikedResponse?>,
                            response: Response<LikedResponse?>
                        ) {
                            if (response.isSuccessful) {

                                like.setImageResource(R.drawable.ic_favorite_fill)
                                when (response.body()?.count) {
                                    0-> noOflikes!!.text=""
                                    1 -> noOflikes!!.text = "liked by ${response.body()?.usr}"
                                    2 -> noOflikes!!.text = "liked by ${response.body()?.usr} and 1 other"
                                    else -> noOflikes!!.text = "liked by ${response.body()?.usr} and ${response.body()!!.count-1} others"
                                }

                            }
                        }

                        override fun onFailure(call: Call<LikedResponse?>, t: Throwable) {
                            Toast.makeText(context, "something went wrong...", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })

                }
            })

            itemView.isSoundEffectsEnabled = false

            //For hiding visibility
            //menu.visibility = View.GONE

            menu.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(v: View) {
            val popupMenus = PopupMenu(context, v)
            popupMenus.inflate(R.menu.feed_menu)
            var position= adapterPosition


            popupMenus.setOnMenuItemClickListener {
                val shredpref =
                    context.getSharedPreferences("ourspace", Context.MODE_PRIVATE)
                val token: String = shredpref.getString("token", null).toString()
                val header = "Bearer $token"
                when (it.itemId) {
                    R.id.hide -> {
                        var adminResponse= ApiClient.userService.adminAction(header,id=posts[position].id,act="hid")
                        adminResponse.enqueue(object : Callback<LikeResponse?> {
                            override fun onResponse(
                                call: Call<LikeResponse?>,
                                response: Response<LikeResponse?>
                            ) {
                                if (response.isSuccessful)
                                {
                                    Toast.makeText(context, "${response.body()?.msg}", Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong, please login again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<LikeResponse?>, t: Throwable) {
                                Toast.makeText(
                                    context,
                                    "Something went wrong, please login again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        true
                    }
                    R.id.delete -> {
                        var adminResponse= ApiClient.userService.adminAction(header,posts[position].id,"del")
                        adminResponse.enqueue(object : Callback<LikeResponse?> {
                            override fun onResponse(
                                call: Call<LikeResponse?>,
                                response: Response<LikeResponse?>
                            ) {
                                if (response.isSuccessful)
                                {
                                    Toast.makeText(context, "${response.body()?.msg}", Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong, please login again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<LikeResponse?>, t: Throwable) {
                                Toast.makeText(
                                    context,
                                    "Something went wrong, please login again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_items_layout, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentUserName = posts[position].username
        val currentUploadTime = posts[position].date
        val currentCaption = posts[position].caption
        holder.userName.text = currentUserName
        holder.uploadTime.text = Utils.getTimeAgo(currentUploadTime)
        holder.caption.text = currentCaption
        val shredpref = context.getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        val token: String = shredpref.getString("token", null).toString()
        val header = "Bearer $token"
        val likeResponse = ApiClient.userService.likePost(header, posts[position].id, "chk")

        likeResponse.enqueue(object : Callback<LikedResponse?> {
            override fun onResponse(
                call: Call<LikedResponse?>,
                response: Response<LikedResponse?>
            ) {
                if (response.isSuccessful) {
                    val res =
                        if (response.body()?.msg.toString() == "1") R.drawable.ic_favorite_fill else R.drawable.ic_favorite_light
                    holder.like.setImageResource(res)
                    when (response.body()?.count) {
                        0-> holder.noOflikes.text=""
                        1 -> holder.noOflikes.text = "liked by ${response.body()?.usr}"
                        2 -> holder.noOflikes.text = "liked by ${response.body()?.usr} and 1 other"
                        else -> holder.noOflikes.text = "liked by ${response.body()?.usr} and ${response.body()!!.count-1} others"
                    }
                }
            }

            override fun onFailure(call: Call<LikedResponse?>, t: Throwable) {
                Toast.makeText(context, "something went wrong...", Toast.LENGTH_SHORT).show()
            }
        })

        holder.like.setOnClickListener { likePost(position, holder = holder) }
        holder.like.isSoundEffectsEnabled = false
        holder.image.adjustViewBounds
        Glide.with(context)
            .load("${BASE_URL}${posts[position].pic}")
            .placeholder(R.drawable.ic_loading)
            .into(holder.image)
        Glide.with(context)
            .load("${BASE_URL}${posts[position].pfp}")
            .placeholder(R.drawable.ic_avatars)
            .circleCrop()
            .into(holder.profilePhoto)

        val bundle = Bundle()
        bundle.putString("username", posts[position].username)

        holder.userName.setOnClickListener {

            Navigation.createNavigateOnClickListener(
                R.id.action_feedFragment_to_userProfileFragment,
                bundle
            ).onClick(holder.userName)
        }

        holder.profilePhoto.setOnClickListener {
            Navigation.createNavigateOnClickListener(
                R.id.action_feedFragment_to_userProfileFragment,
                bundle
            ).onClick(holder.profilePhoto)
        }

    }

    override fun getItemCount(): Int {
        return posts.size
    }

    abstract class DoubleClickListener : View.OnClickListener {
        private var lastClickTime: Long = 0
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

    private fun likePost(
        position: Int,
        holder: ItemViewHolder? = null,
        like: ImageView? = null,
        noOflikes: TextView? = null,

        ) {


        val id = posts[position].id
        val shredpref = context.getSharedPreferences("ourspace", Context.MODE_PRIVATE)
        val token: String = shredpref.getString("token", null).toString()
        val header = "Bearer $token"
        val likeResponse = ApiClient.userService.likePost(header, id, "like")


        likeResponse.enqueue(object : Callback<LikedResponse?> {
            override fun onResponse(
                call: Call<LikedResponse?>,
                response: Response<LikedResponse?>
            ) {
                if (response.isSuccessful) {
                    val res =
                        if (response.body()?.msg.toString() == "1") R.drawable.ic_favorite_light else R.drawable.ic_favorite_fill

                    if (like == null) {
                        holder!!.like.setImageResource(res)

                        when (response.body()?.count) {
                            0-> holder.noOflikes!!.text=""
                            1 -> holder.noOflikes.text = "liked by ${response.body()?.usr}"
                            2 -> holder.noOflikes.text = "liked by ${response.body()?.usr} and 1 other"
                            else -> holder.noOflikes.text = "liked by ${response.body()?.usr} and ${response.body()!!.count-1} others"
                        }

                    } else {
                        when (response.body()?.count) {
                            0-> noOflikes!!.text=""
                            1 -> noOflikes!!.text = "liked by ${response.body()?.usr}"
                            2 -> noOflikes!!.text = "liked by ${response.body()?.usr} and 1 other"
                            else -> noOflikes!!.text = "liked by ${response.body()?.usr} and ${response.body()!!.count-1} others"
                        }
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

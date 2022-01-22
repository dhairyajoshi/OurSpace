package com.example.ourspace.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ourspace.R
import com.example.ourspace.models.Utils.Companion.getTimeAgo

import com.example.ourspace.retrofit.ApiClient
import com.example.ourspace.retrofit.NotificationResponse


class NotificationsRVAdapter(
    var context: Context,
    private val notificationlist: List<NotificationResponse>
) :
    RecyclerView.Adapter<NotificationsRVAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.notifications_item_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = notificationlist[position]
        holder.name.text = "${currentItem.sender} liked your post"
        holder.time.text = getTimeAgo(currentItem.date)
//        holder.time.text = currentItem.date
        Glide.with(context)
            .load("${ApiClient.BASE_URL}${currentItem.pic}")
            .placeholder(R.drawable.ic_logo)
            .circleCrop()
            .into(holder.userpfp);

    }

    override fun getItemCount(): Int {
        return notificationlist.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userpfp: ImageView = itemView.findViewById(R.id.profilePhoto)
        val name: TextView = itemView.findViewById(R.id.likedBy)
        val time: TextView = itemView.findViewById(R.id.time)
    }
}

package com.example.ourspace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ourspace.R
import com.example.ourspace.models.NewNotifications


class NotificationsRVAdapter(private val notificationlist: ArrayList<NewNotifications>) :
    RecyclerView.Adapter<NotificationsRVAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.notifications_item_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = notificationlist[position]
        holder.userpfp.setImageResource(currentItem.userpfp)
        holder.name.text = currentItem.name
        holder.time.text = currentItem.time

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

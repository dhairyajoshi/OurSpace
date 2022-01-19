package com.example.ourspace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ourspace.R

class FeedRVAdapter(private val userNames: Array<String>,private val uploadTimes: Array<String>,private val captions: Array<String>) : RecyclerView.Adapter<FeedRVAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val uploadTime: TextView = view.findViewById(R.id.time)
        val caption: TextView = view.findViewById(R.id.caption)
        val profilePhoto : ImageView = view.findViewById(R.id.profilePhoto)
        val image : ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_items_layout, parent, false)

        return ItemViewHolder(adapterLayout)    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentUserName = userNames[position]
        val currentUploadTime = uploadTimes[position]
        val currentCaption = captions[position]
        holder.userName.text = currentUserName
        holder.uploadTime.text = currentUploadTime
        holder.caption.text = currentCaption
    }

    override fun getItemCount(): Int {
        return userNames.size
    }
}
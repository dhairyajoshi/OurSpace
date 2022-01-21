package com.example.ourspace

import android.app.Activity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.os.Bundle
import android.view.View
import com.example.ourspace.R
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import org.json.JSONArray

class TimelineActivity : Activity() {
    private var swipeContainer: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Only ever call `setContentView` once right at the top
        setContentView(R.layout.activity_main)
        // Lookup the swipe container view
        swipeContainer = findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        // Setup refresh listener which triggers new data loading
        swipeContainer!!.setOnRefreshListener { // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
        }
        // Configure the refreshing colors
        swipeContainer!!.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }
    
}
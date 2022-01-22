package com.example.ourspace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ourspace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val PICK_IMAGE: Int = 100
        const val PICK_CFP: Int = 200
        const val PICK_IMG: Int = 300

        fun newInstance(PFP: Int, CFP: Int, IMG: Int) = ProfileFragment().apply() {
            arguments = Bundle(3).apply {
                putInt("PFP_IMAGE", PFP)
                putInt("CFP_IMAGE", CFP)
                putInt("PICK_IMG", IMG)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}
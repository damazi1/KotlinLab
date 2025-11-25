package com.example.labirynt

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.labirynt.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Przycisk START - przejście do MainActivity
        binding.startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Przycisk EXIT - zamknięcie aplikacji
        binding.exitButton.setOnClickListener {
            finishAffinity() // Zamyka wszystkie aktywności i wychodzi z aplikacji
        }
    }
}
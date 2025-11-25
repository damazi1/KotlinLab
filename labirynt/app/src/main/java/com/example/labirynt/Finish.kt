package com.example.labirynt

    import android.content.Intent
    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import com.example.labirynt.databinding.ActivityFinishBinding

    class Finish : AppCompatActivity() {
        private lateinit var binding: ActivityFinishBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityFinishBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Pobierz dane z intent WEWNĄTRZ onCreate()
            val stepCount = intent.getIntExtra("STEP_COUNT", 0)

            // Ustawienie tekstu z liczbą kroków
            binding.stepCountTextView.text = "Liczba kroków: $stepCount"

            binding.backToMenuButton.setOnClickListener { v ->
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
            }
        }
    }
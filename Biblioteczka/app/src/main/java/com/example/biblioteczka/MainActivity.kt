package com.example.biblioteczka

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicjalizacja bazy danych
        dbHelper = DatabaseHelper(this)

        // Przykład użycia
        dbHelper.addBook("1x22","Pan Tadeusz", "Adam Mickiewicz", 1834, "desc", "webP")
        dbHelper.addBook("3x34","Lalka", "Bolesław Prus", 1890, "desc", "webPage")

        // Pobierz wszystkie książki
        val books = dbHelper.getAllBooks()
        books.forEach {
            println("${it.title} - ${it.author} (${it.year})")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
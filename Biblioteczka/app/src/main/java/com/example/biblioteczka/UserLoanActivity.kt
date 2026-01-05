package com.example.biblioteczka

import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UserLoansActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_loans)

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        val contactId = intent.getStringExtra("contactId") ?: return
        val katalog = Katalog(this)

        val recycler = findViewById<RecyclerView>(R.id.recyclerUserLoans)
        recycler.layoutManager = LinearLayoutManager(this)

        val books = loadBooksFromDb(katalog)
        val loans = loadLoans(katalog)
            .filter { it.contactId == contactId && it.returnDate == null }
        val joined = loans.mapNotNull { loan ->
            books.firstOrNull { it.id == loan.bookId }?.let { BookWithLoan(it, loan) }
        }

        val adapter = UserLoanAdapter(joined)
        recycler.adapter = adapter

        findViewById<Button>(R.id.btnReturnSelected).setOnClickListener {
            val selected = adapter.getSelectedLoans()
            if (selected.isEmpty()) return@setOnClickListener
            selected.forEach { loan ->
                returnBook(loan.bookId, katalog, this)
            }
            finish()
        }
    }
}
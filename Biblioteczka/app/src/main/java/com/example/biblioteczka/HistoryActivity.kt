package com.example.biblioteczka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {
    private lateinit var katalog: Katalog
    private lateinit var booksById: Map<String, Book>
    private lateinit var contactsById: Map<String, Contact>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        katalog = Katalog(this)
        booksById = loadBooks().associateBy { it.id }
        contactsById = ContactsHelper(this).getAllContacts().associateBy { it.id }
        val loans = loadLoans()

        val recycler = findViewById<RecyclerView>(R.id.recyclerHistory)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = HistoryAdapter(loans, booksById, contactsById)
    }

    private fun loadLoans(): List<Loan> {
        val out = mutableListOf<Loan>()
        val db = katalog.readableDatabase
        val c = db.rawQuery(
            "SELECT id, book_id, contact_id, data, return_date, status, planned_return_date FROM wypozyczenia",
            null
        )
        c.use { cur ->
            val idIdx = cur.getColumnIndexOrThrow("id")
            val bookIdx = cur.getColumnIndexOrThrow("book_id")
            val contactIdx = cur.getColumnIndexOrThrow("contact_id")
            val dateIdx = cur.getColumnIndexOrThrow("data")
            val returnDateIdx = cur.getColumnIndexOrThrow("return_date")
            val statusIdx = cur.getColumnIndexOrThrow("status")
            val plannedIdx = cur.getColumnIndexOrThrow("planned_return_date")
            while (cur.moveToNext()) {
                out.add(
                    Loan(
                        cur.getLong(idIdx),
                        cur.getString(bookIdx),
                        cur.getString(contactIdx),
                        cur.getString(dateIdx),
                        cur.getString(returnDateIdx),
                        cur.getString(statusIdx),
                        cur.getString(plannedIdx)
                    )
                )
            }
        }
        db.close()
        return out
    }

    private fun loadBooks(): List<Book> {
        val list = mutableListOf<Book>()
        val db = katalog.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, tytul, autor, rok_wydania, opis, url FROM ksiazki",
            null
        )
        cursor.use { c ->
            val idIdx = c.getColumnIndexOrThrow("id")
            val tytulIdx = c.getColumnIndexOrThrow("tytul")
            val autorIdx = c.getColumnIndexOrThrow("autor")
            val rokIdx = c.getColumnIndexOrThrow("rok_wydania")
            val opisIdx = c.getColumnIndexOrThrow("opis")
            val urlIdx = c.getColumnIndexOrThrow("url")
            while (c.moveToNext()) {
                list.add(
                    Book(
                        c.getString(idIdx),
                        c.getString(tytulIdx),
                        c.getString(autorIdx),
                        c.getInt(rokIdx),
                        c.getString(opisIdx),
                        c.getString(urlIdx)
                    )
                )
            }
        }
        db.close()
        return list
    }
}
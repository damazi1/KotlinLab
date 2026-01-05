package com.example.biblioteczka

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {
    private lateinit var katalog: Katalog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        katalog = Katalog(this)
        val loans = loadLoans()

        val recycler = findViewById<RecyclerView>(R.id.recyclerHistory)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = HistoryAdapter(loans)
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
}
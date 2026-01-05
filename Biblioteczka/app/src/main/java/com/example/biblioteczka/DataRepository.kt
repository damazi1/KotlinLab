package com.example.biblioteczka

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDate

fun loadBooksFromDb(katalog: Katalog): List<Book> {
    val list = mutableListOf<Book>()
    val db = katalog.readableDatabase
    val cursor = db.rawQuery("SELECT id, tytul, autor, rok_wydania, opis, url FROM ksiazki", null)
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

fun loadLoans(katalog: Katalog): List<Loan> {
    val out = mutableListOf<Loan>()
    val db = katalog.readableDatabase
    val c = db.rawQuery("SELECT id, book_id, contact_id, data, return_date, status, planned_return_date FROM wypozyczenia", null)
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

@RequiresApi(Build.VERSION_CODES.O)
fun borrowBook(bookId: String, contactId: String, plannedReturnDate: String?, katalog: Katalog) {
    val db = katalog.writableDatabase
    val values = ContentValues().apply {
        put("book_id", bookId)
        put("contact_id", contactId)
        put("planned_return_date", plannedReturnDate)
    }
    db.insertWithOnConflict("wypozyczenia", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    db.close()
}

@RequiresApi(Build.VERSION_CODES.O)
fun returnBook(bookId: String, katalog: Katalog, ctx: Context) {
    val db = katalog.writableDatabase
    val cv = ContentValues().apply {
        put("return_date", LocalDate.now().toString())
        put("status", "returned")
    }
    val rows = db.update(
        "wypozyczenia",
        cv,
        "book_id=? AND return_date IS NULL",
        arrayOf(bookId)
    )
    if (rows == 0) {
        Toast.makeText(ctx, "Ta książka nie była aktywnie wypożyczona", Toast.LENGTH_SHORT).show()
    }
    db.close()
}
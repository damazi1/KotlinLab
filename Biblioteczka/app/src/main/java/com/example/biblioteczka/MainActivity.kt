package com.example.biblioteczka

import android.Manifest
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import kotlin.compareTo
import kotlin.text.insert

class MainActivity : AppCompatActivity() {
    private val requestContactsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) bindContacts()
    }
//    private lateinit var dbHelper: DatabaseHelper

    private lateinit var katalog: Katalog
    private lateinit var contactsAdapter: ContactAdapter
    private lateinit var bookAdapter: BookAdapter
    private var contacts: List<Contact> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)

        katalog = Katalog(this)
        val db = katalog.writableDatabase  // tworzy bazę i tabelę, jeśli nie istnieje

        // przykład zapisu
        val newBooks = List<Book>(10) { i ->
            Book(
                id = "1x$i",
                title = "Tytuł książki $i",
                author = "Autor $i",
                year = 2000 + i,
                description = "Opis książki $i",
                url = "https://example.com/book$i"
            )
        }
        newBooks.forEach { book ->
            insertBookToDb(book, db)
        }
        val newBook = Book(id="3x2", title="Gra o tron", author="George R. R. Martin", year=1996,
            description="Pierwsza część sagi Pieśń Lodu i Ognia, na podstawie której powstał serial Gra o tron.",
            url="https://pl.wikipedia.org/wiki/Gra_o_tron_(powie%C5%9B%C4%87)")
        insertBookToDb(newBook, db)
        db.close()
//
//        borrowBook("3x233", "2") // przykład wypożyczenia książki kontaktowi o id 1


        katalog = Katalog(this)
        val books = loadBooksFromDb()
        val loans = loadLoans()

        val recyclerBooks = findViewById<RecyclerView>(R.id.recyclerBooks)
        recyclerBooks.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookAdapter(books, loans, contacts)
        recyclerBooks.adapter = bookAdapter

        val recyclerContacts = findViewById<RecyclerView>(R.id.recyclerContacts)
        recyclerContacts.layoutManager = LinearLayoutManager(this)
        contactsAdapter = ContactAdapter(contacts) { contact ->
            val toBorrow = bookAdapter.selectedBookIds()
            toBorrow.forEach { bookId -> borrowBook(bookId, contact.id) }
            bookAdapter.updateLoans(loadLoans())
        }
        recyclerContacts.adapter = contactsAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onResume() {
        super.onResume()
        bookAdapter.updateLoans(loadLoans())
    }

    private fun insertBookToDb(book: Book, db: SQLiteDatabase) {
        val values = android.content.ContentValues().apply {
            put("id", book.id)            // pozycja na półce
            put("tytul", book.title)
            put("autor", book.author)
            put("rok_wydania", book.year)
            put("opis", book.description)
            put("url", book.url)
        }
        val result = db.insertWithOnConflict(
            "ksiazki",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE // nie rzuca wyjątku przy duplikacie
        )
        if (result == -1L) {
            android.widget.Toast.makeText(this, "Ta pozycja jest już zajęta", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    fun loadBooksFromDb(): List<Book> {
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
                val id = c.getString(idIdx)
                val tytul = c.getString(tytulIdx)
                val autor = c.getString(autorIdx)
                val rok = c.getInt(rokIdx)
                val opis = c.getString(opisIdx)
                val url = c.getString(urlIdx)
                list.add(Book(id, tytul, autor, rok, opis, url))
            }
        }
        db.close()
        return list
    }
    private fun bindContacts() {
        val contacts = ContactsHelper(this).getAllContacts()
        contactsAdapter.update(contacts)
        val recyclerAdapter = (findViewById<RecyclerView>(R.id.recyclerBooks).adapter as? BookAdapter)
        recyclerAdapter?.updateContacts(contacts)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun borrowBook(bookId: String, contactId: String) {
        val db = katalog.writableDatabase
        val values = android.content.ContentValues().apply {
            put("book_id", bookId)
            put("contact_id", contactId)
        }
        try {
            val result = db.insertWithOnConflict(
                "wypozyczenia",
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
            )
            if (result == -1L) {
                android.widget.Toast.makeText(this, "Ta ksiazka jest wypozyczona lub nie istnieje", android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            android.widget.Toast.makeText(this, "Brak książki o id $bookId", android.widget.Toast.LENGTH_SHORT).show()
        } finally {
            db.close()
        }
    }


    fun loadLoans(): List<Loan> {
        val out = mutableListOf<Loan>()
        val db = katalog.readableDatabase
        val c = db.rawQuery(
            "SELECT id, book_id, contact_id, data, return_date, status FROM wypozyczenia",
            null
        )
        c.use { cur ->
            val idIdx = cur.getColumnIndexOrThrow("id")
            val bookIdx = cur.getColumnIndexOrThrow("book_id")
            val contactIdx = cur.getColumnIndexOrThrow("contact_id")
            val dateIdx = cur.getColumnIndexOrThrow("data")
            val returnDateIdx = cur.getColumnIndexOrThrow("return_date")
            val statusIdx = cur.getColumnIndexOrThrow("status")
            while (cur.moveToNext()) {
                out.add(
                    Loan(
                        cur.getLong(idIdx),
                        cur.getString(bookIdx),
                        cur.getString(contactIdx),
                        cur.getString(dateIdx),
                        cur.getString(returnDateIdx),
                        cur.getString(statusIdx)
                    )
                )
            }
        }
        db.close()
        return out
    }
}
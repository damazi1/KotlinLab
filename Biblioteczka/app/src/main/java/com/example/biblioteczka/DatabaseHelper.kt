package com.example.biblioteczka

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "biblioteczka.db"
        private const val DATABASE_VERSION = 2

        // Tabela książek
        const val TABLE_BOOKS = "books"
        const val COLUMN_ID = "id"
        const val COLUMN_POS = "pos"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_YEAR = "year"
        const val COLUMN_DESC = "description"
        const val COLUMN_WEB_PAGE = "webPage"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_POS TEXT NOT NULL,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_AUTHOR TEXT NOT NULL,
                $COLUMN_YEAR INTEGER,
                $COLUMN_DESC TEXT,
                $COLUMN_WEB_PAGE TEXT
                
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        onCreate(db)
    }

    // Dodaj książkę
    fun addBook(pos: String, title: String, author: String, year: Int, desc: String, webPage: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_POS, pos)
            put(COLUMN_TITLE, title)
            put(COLUMN_AUTHOR, author)
            put(COLUMN_YEAR, year)
            put(COLUMN_DESC, desc)
            put(COLUMN_WEB_PAGE, webPage)
        }
        return db.insert(TABLE_BOOKS, null, values)
    }

    // Pobierz wszystkie książki
    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = readableDatabase
        val cursor = db.query(TABLE_BOOKS, null, null, null, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                val book = Book(
                    pos = it.getString(it.getColumnIndexOrThrow(COLUMN_POS)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    author = it.getString(it.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                    year = it.getInt(it.getColumnIndexOrThrow(COLUMN_YEAR)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESC)),
                    webPage = it.getString(it.getColumnIndexOrThrow(COLUMN_WEB_PAGE))
                )
                books.add(book)
            }
        }
        return books
    }

    // Usuń książkę
    fun deleteBook(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_BOOKS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Aktualizuj książkę
    fun updateBook(id: Int, title: String, author: String, year: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_AUTHOR, author)
            put(COLUMN_YEAR, year)
        }
        return db.update(TABLE_BOOKS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}
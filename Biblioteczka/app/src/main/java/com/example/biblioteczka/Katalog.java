package com.example.biblioteczka;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Katalog extends SQLiteOpenHelper {
    private static final int version = 18;
    public Katalog(Context context) {
        super(context, "biblioteczka.db", null, version);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // PRAGMA foreign_keys=ON
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS ksiazki " +
                    "(id TEXT PRIMARY KEY," +
                    "tytul TEXT," +
                    "autor TEXT," +
                    "rok_wydania INTEGER," +
                    "opis TEXT," +
                    "url TEXT" +
                    ");");
            db.execSQL("CREATE TABLE IF NOT EXISTS wypozyczenia " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "book_id TEXT NOT NULL," +
                    "contact_id TEXT NOT NULL," +
                    "data TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "return_date TEXT," +
                    "planned_return_date TEXT," +
                    "status TEXT DEFAULT 'borrowed'," +
                    "FOREIGN KEY(book_id) REFERENCES ksiazki(id) ON DELETE CASCADE" +
                    ");");
        } catch (SQLiteException e) {
            Log.e("Katalog", "Błąd podczas tworzenia tabel", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("Katalog", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("drop table if exists ksiazki");
        db.execSQL("DROP TABLE IF EXISTS wypozyczenia");
        onCreate(db);
    }
    public void markLoanReturned(long loanId, String returnDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("return_date", returnDate);
        cv.put("status", "returned");
        db.update("wypozyczenia", cv, "id=?", new String[]{String.valueOf(loanId)});
    }
}



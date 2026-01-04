package com.example.biblioteczka

import android.content.Context
import android.provider.ContactsContract

data class Contact(
    val id: String,
    val name: String,
    val phone: String?
)

class ContactsHelper(private val context: Context) {

    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                contacts.add(Contact(id, name, null))
            }
        }
        return contacts
    }
}
package com.example.biblioteczka

import android.content.Context
import android.provider.ContactsContract

data class Contact(
    val id: String,
    val name: String,
    val address: String?,
    val mail: String?,
    val phone: String?
)

class ContactsHelper(private val context: Context) {

    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val idIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val id = it.getString(idIdx)
                val name = it.getString(nameIdx)
                val phone = it.getString(phoneIdx)

                val mail = resolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                    "${ContactsContract.CommonDataKinds.Email.CONTACT_ID}=?",
                    arrayOf(id),
                    null
                )?.use { eCur ->
                    if (eCur.moveToFirst())
                        eCur.getString(eCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    else null
                }

                val address = resolver.query(
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS),
                    "${ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID}=?",
                    arrayOf(id),
                    null
                )?.use { aCur ->
                    if (aCur.moveToFirst())
                        aCur.getString(aCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS))
                    else null
                }

                contacts.add(Contact(id, name, address, mail, phone))
            }
        }
        return contacts
    }
}
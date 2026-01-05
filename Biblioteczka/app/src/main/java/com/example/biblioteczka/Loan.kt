package com.example.biblioteczka

data class Loan(
    val id: Long,
    val bookId: String,
    val contactId: String,
    val date: String,
    val returnDate: String?,
    val status: String
)
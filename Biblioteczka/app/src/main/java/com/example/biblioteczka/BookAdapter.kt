package com.example.biblioteczka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private var items: List<Book>,
    private var loans: List<Loan>,
    private var contacts: List<Contact>
) : RecyclerView.Adapter<BookAdapter.VH>() {

    private val selected = mutableSetOf<String>() // book ids

    private fun activeLoanForBook(bookId: String): Loan? =
        loans.firstOrNull { it.bookId == bookId && it.returnDate.isNullOrEmpty() }

    private fun contactForBook(bookId: String): Contact? {
        val loan = activeLoanForBook(bookId) ?: return null
        return contacts.firstOrNull { it.id == loan.contactId }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cb: CheckBox = itemView.findViewById(R.id.cbSelected)
        val tvId: TextView = itemView.findViewById(R.id.tvId)
        val tvDesciption: TextView = itemView.findViewById(R.id.tvDesciption)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val b = items[position]
        val contact = contactForBook(b.id)
        holder.tvId.text = "[${b.id}] - ${b.title} — ${b.author} - ${b.year} r."
        holder.tvDesciption.text = b.description
        holder.tvStatus.text = contact?.let { "${it.name} (${it.phone ?: "brak numeru"})" }
            ?: "Niewypożyczona"

        holder.cb.setOnCheckedChangeListener(null)
        holder.cb.isChecked = selected.contains(b.id)
        holder.cb.isEnabled = contact == null // zablokuj zaznaczenie, gdy jest aktywne wypożyczenie
        holder.cb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selected.add(b.id) else selected.remove(b.id)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateBooks(newItems: List<Book>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updateLoans(newLoans: List<Loan>) {
        loans = newLoans
        notifyDataSetChanged()
    }

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    fun selectedBookIds(): List<String> = selected.toList()
}
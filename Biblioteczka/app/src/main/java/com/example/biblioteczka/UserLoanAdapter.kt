package com.example.biblioteczka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class BookWithLoan(val book: Book, val loan: Loan)

class UserLoanAdapter(private var items: List<BookWithLoan>) :
    RecyclerView.Adapter<UserLoanAdapter.VH>() {

    private val selectedIds = mutableSetOf<Long>()

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val title = v.findViewById<TextView>(R.id.tvBookTitle)
        private val contact = v.findViewById<TextView>(R.id.tvBookDetails)
        private val loanDate = v.findViewById<TextView>(R.id.tvLoanInfo)
        private val cb = v.findViewById<CheckBox>(R.id.cbReturn)

        fun bind(item: BookWithLoan) {
            title.text = item.book.title
            contact.text = item.book.author + " - " + item.book.year + " r."
            loanDate.text = item.loan.date
            cb.setOnCheckedChangeListener(null)
            cb.isChecked = selectedIds.contains(item.loan.id)
            cb.setOnCheckedChangeListener { _, checked ->
                if (checked) selectedIds.add(item.loan.id) else selectedIds.remove(item.loan.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_user_loan, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    fun getSelectedLoans(): List<Loan> =
        items.filter { selectedIds.contains(it.loan.id) }.map { it.loan }
}
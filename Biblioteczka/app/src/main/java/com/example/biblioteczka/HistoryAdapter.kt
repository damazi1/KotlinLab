package com.example.biblioteczka

    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView

    class HistoryAdapter(
        private val items: List<Loan>,
        private val books: Map<String, Book>,
        private val contacts: Map<String, Contact>
    ) : RecyclerView.Adapter<HistoryAdapter.VH>() {

        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val title: TextView = v.findViewById(R.id.txtHistoryTitle)
            val subtitle: TextView = v.findViewById(R.id.txtHistorySubtitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val loan = items[position]
            val bookTitle = books[loan.bookId]?.title ?: "ID: ${loan.bookId}"
            val contactName = contacts[loan.contactId]?.name ?: "ID: ${loan.contactId}"
            holder.title.text = "Książka: $bookTitle"
            holder.subtitle.text =
                "Kontakt: $contactName | Od: ${loan.date} | Zwrot: ${loan.returnDate ?: "-"} | Plan: ${loan.plannedReturnDate ?: "-"} | Status: ${loan.status}"
        }

        override fun getItemCount(): Int = items.size
    }
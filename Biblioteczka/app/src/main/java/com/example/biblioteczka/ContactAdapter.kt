package com.example.biblioteczka

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private var items: List<Contact>,
                     private val onBorrowClicked: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.VH>() {
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvPhone: TextView = v.findViewById(R.id.tvPhone)
        val btnBorrow: Button = itemView.findViewById(R.id.btnBorrow)
        val btnReturn: Button = itemView.findViewById(R.id.btnReturn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.tvName.text = "[${c.id}] - ${c.name} - ${c.phone ?: "brak numeru"}"
        holder.tvPhone.text = "${c.mail ?: "brak maila"} - ${c.address ?: "brak adresu"}"
        holder.btnBorrow.setOnClickListener { onBorrowClicked(c) }
        holder.btnReturn.setOnClickListener {
            val ctx = it.context
            val intent = Intent(ctx, UserLoansActivity::class.java)
            intent.putExtra("contactId", c.id)
            ctx.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Contact>) {
        items = newItems
        notifyDataSetChanged()
    }
}
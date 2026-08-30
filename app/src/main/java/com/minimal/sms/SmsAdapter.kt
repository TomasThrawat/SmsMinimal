package com.minimal.sms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsAdapter(private var items: List<SmsMessage>) :
    RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm  dd/MM", Locale.getDefault())

    class SmsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sender: TextView = view.findViewById(R.id.sender)
        val body: TextView = view.findViewById(R.id.body)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val item = items[position]
        holder.sender.text = item.sender
        holder.body.text = item.body
        holder.timestamp.text = timeFormat.format(Date(item.timestamp))
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<SmsMessage>) {
        items = newItems
        notifyDataSetChanged()
    }
}

package com.example.recentcalls

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recentcalls.data.CallRecord

class CallRecordAdapter(private val callRecords: List<CallRecord>) : RecyclerView.Adapter<CallRecordAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val name: TextView = view.findViewById(R.id.name)
        val phoneNumber: TextView = view.findViewById(R.id.phone_number)
        val callTime: TextView = view.findViewById(R.id.call_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_call, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val callRecord = callRecords[position]
        holder.avatar.setImageResource(callRecord.avatar)
        holder.name.text = callRecord.name
        holder.phoneNumber.text = callRecord.phoneNumber
        holder.callTime.text = callRecord.callTime
    }

    override fun getItemCount() = callRecords.size

    fun getDataList() = callRecords.toMutableList()
}
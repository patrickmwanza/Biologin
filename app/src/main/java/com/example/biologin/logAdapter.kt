package com.example.biologin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class logAdapter(private val dataList: ArrayList<logdata>) : RecyclerView.Adapter<logAdapter.ViewHolder>() {

    private val data = mutableListOf<logdata>()

    // Add a function to update the data in the adapter
    fun updateData(newData: List<logdata>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val date: TextView = itemView.findViewById(R.id.dateid)
            val timein: TextView = itemView.findViewById(R.id.timeinid)
            val timeout: TextView = itemView.findViewById(R.id.timeoutid)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleritem, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item=dataList [position]
            holder.date.text=item.date
            holder.timein.text=item.timein
            holder.timeout.text=item.timeout
        }

        override fun getItemCount(): Int {
            return dataList.size
        }
    }

package com.cloudin.monsterchicken.commonclass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R

class ErrorListAdapter(private val errorList: List<String>) :
    RecyclerView.Adapter<ErrorListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.row_error_dialog, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(errorList[position])
    }

    override fun getItemCount(): Int {
        return errorList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(errorList: String) {
            val tv_error = itemView.findViewById(R.id.tv_error) as TextView
            tv_error.text = errorList
        }
    }
}
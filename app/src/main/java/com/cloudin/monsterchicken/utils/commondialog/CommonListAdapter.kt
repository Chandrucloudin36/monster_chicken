package com.cloudin.monsterchicken.utils.commondialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R

class CommonListAdapter(private val commonList: List<CommonListResponse.CommonList>) :
    RecyclerView.Adapter<CommonListAdapter.ViewHolder>() {
    var onItemClick: ((CommonListResponse.CommonList) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_common_list_dialog, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(commonList[position])
    }

    override fun getItemCount(): Int {
        return commonList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(commonListItem: CommonListResponse.CommonList) {
            val tvCommonListName = itemView.findViewById(R.id.tvCommonListName) as TextView
            val llCommonListParent = itemView.findViewById(R.id.llCommonListParent) as LinearLayout
            tvCommonListName.text = commonListItem.name
            llCommonListParent.setOnClickListener {
                onItemClick?.invoke(commonListItem)
            }
        }
    }
}
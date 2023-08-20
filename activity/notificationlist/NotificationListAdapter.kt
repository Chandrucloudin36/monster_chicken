package com.cloudin.monsterchicken.activity.notificationlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.databinding.RowNotificationListBinding

class NotificationListAdapter(val viewModel: NotificationListViewModel) :
    ListAdapter<Notifications, NotificationListAdapter.EmployeeViewHolder>(
        TaskDiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmployeeViewHolder {
        return EmployeeViewHolder.from(
            parent
        )
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)

        if (item.is_read == 1)
            holder.binding.vwNotificationRead.visibility = View.GONE
        else
            holder.binding.vwNotificationRead.visibility = View.VISIBLE

        holder.binding.rlNotificationListParent.setOnClickListener {
            viewModel.updateNotificationStatus(selectedId = position)
        }

        if (position == itemCount - 1) holder.binding.vwDivide.visibility = View.GONE
        else holder.binding.vwDivide.visibility = View.VISIBLE
    }

    class EmployeeViewHolder private constructor(val binding: RowNotificationListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: NotificationListViewModel, item: Notifications) {
            binding.viewModels = viewModels
            binding.notificationsDetail = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowNotificationListBinding.inflate(layoutInflater, parent, false)
                return EmployeeViewHolder(
                    binding
                )
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Notifications>() {
        override fun areItemsTheSame(
            oldItem: Notifications,
            newItem: Notifications
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Notifications,
            newItem: Notifications
        ): Boolean {
            return oldItem == newItem
        }
    }
}
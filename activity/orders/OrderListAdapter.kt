package com.cloudin.monsterchicken.activity.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.databinding.RowOrdersListBinding

class OrderListAdapter(val viewModel: OrdersListViewModel) :
    ListAdapter<OrderList, OrderListAdapter.EmployeeViewHolder>(
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
    }

    class EmployeeViewHolder private constructor(val binding: RowOrdersListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: OrdersListViewModel, item: OrderList) {
            binding.viewModels = viewModels
            binding.ordersList = item
            val orderListItemAdapter = OrderListItemAdapter(viewModels)
            binding.rvOrdersItemList.adapter = orderListItemAdapter

            if (adapterPosition % 2 == 1)
                binding.llOrdersDetails.setBackgroundColor(
                    ResourcesCompat.getColor(
                        binding.llOrdersDetails.resources,
                        R.color.c1AF8B31A,
                        null
                    )
                )
            else
                binding.llOrdersDetails.setBackgroundColor(
                    ResourcesCompat.getColor(
                        binding.llOrdersDetails.resources,
                        R.color.white,
                        null
                    )
                )

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowOrdersListBinding.inflate(layoutInflater, parent, false)
                return EmployeeViewHolder(
                    binding
                )
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<OrderList>() {
        override fun areItemsTheSame(
            oldItem: OrderList,
            newItem: OrderList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: OrderList,
            newItem: OrderList
        ): Boolean {
            return oldItem == newItem
        }
    }
}
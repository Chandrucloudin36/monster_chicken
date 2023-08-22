package com.cloudin.monsterchicken.activity.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.databinding.RowOrderedItemListBinding

class OrderListItemAdapter(private val viewModel: OrdersListViewModel) :
    ListAdapter<ProductsItem, OrderListItemAdapter.EmployeeViewHolder>(
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

    class EmployeeViewHolder private constructor(val binding: RowOrderedItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: OrdersListViewModel, item: ProductsItem) {
            binding.viewModels = viewModels
            binding.ordersList = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowOrderedItemListBinding.inflate(layoutInflater, parent, false)
                return EmployeeViewHolder(
                    binding
                )
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<ProductsItem>() {
        override fun areItemsTheSame(
            oldItem: ProductsItem,
            newItem: ProductsItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ProductsItem,
            newItem: ProductsItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
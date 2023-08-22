package com.cloudin.monsterchicken.activity.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.databinding.RowCheckoutCartItemListBinding

class CheckoutCartListAdapter(private val viewModel: CheckoutViewModel) :
    ListAdapter<CartDetails, CheckoutCartListAdapter.EmployeeViewHolder>(
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

        val tvProductPrice =
            holder.binding.root.findViewById<TextView>(R.id.tvProductPrice)
        val tvProductQuantity =
            holder.binding.root.findViewById<TextView>(R.id.tvProductQuantity)

        tvProductPrice.text = "₹ ${item.totalPrice}"
        tvProductQuantity.text = "${item.quantity}"

        holder.bind(viewModel, item)
    }

    class EmployeeViewHolder private constructor(val binding: RowCheckoutCartItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: CheckoutViewModel, item: CartDetails) {
            binding.viewModels = viewModels
            binding.cartList = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowCheckoutCartItemListBinding.inflate(layoutInflater, parent, false)
                return EmployeeViewHolder(
                    binding
                )
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<CartDetails>() {
        override fun areItemsTheSame(
            oldItem: CartDetails,
            newItem: CartDetails
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CartDetails,
            newItem: CartDetails
        ): Boolean {
            return oldItem == newItem
        }
    }
}
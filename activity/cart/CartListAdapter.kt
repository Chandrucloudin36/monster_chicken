package com.cloudin.monsterchicken.activity.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.databinding.RowCartItemListBinding

class CartListAdapter(private val viewModel: CartViewModel) :
    ListAdapter<CartDetails, CartListAdapter.EmployeeViewHolder>(
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
        val tvOutOfStock =
            holder.binding.root.findViewById<TextView>(R.id.tvOutOfStock)
        val ivRemoveCount =
            holder.binding.root.findViewById<ImageView>(R.id.ivRemoveCount)
        val ivAddCount =
            holder.binding.root.findViewById<ImageView>(R.id.ivAddCount)
        val ivRemoveCartItem =
            holder.binding.root.findViewById<ImageView>(R.id.ivRemoveCartItem)
        val llProductAddRemove =
            holder.binding.root.findViewById<LinearLayout>(R.id.llProductAddRemove)

        if (item.stock == item.quantity)
            ivAddCount.visibility = View.INVISIBLE
        else
            ivAddCount.visibility = View.VISIBLE

        ivRemoveCount.setOnClickListener {
            viewModel.addToCart(item, position, false)
        }
        ivAddCount.setOnClickListener {
            viewModel.addToCart(item, position, true)
        }
        ivRemoveCartItem.setOnClickListener {
            item.quantity = 0
            viewModel.addToCart(item, position, true)
        }
        tvProductPrice.text = "₹ ${item.totalPrice}"
        tvProductQuantity.text = "${item.quantity}"

        if (item.quantity == 0) {
            llProductAddRemove.visibility = View.GONE
            tvOutOfStock.visibility = View.VISIBLE
        } else {
            llProductAddRemove.visibility = View.VISIBLE
            tvOutOfStock.visibility = View.GONE
        }

        holder.bind(viewModel, item)
    }

    class EmployeeViewHolder private constructor(val binding: RowCartItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: CartViewModel, item: CartDetails) {
            binding.viewModels = viewModels
            binding.cartList = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowCartItemListBinding.inflate(layoutInflater, parent, false)
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
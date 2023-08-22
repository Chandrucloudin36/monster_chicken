package com.cloudin.monsterchicken.activity.productlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.dashboard.ui.home.CategoriesList
import com.cloudin.monsterchicken.databinding.RowProductCategoriesBinding

class ProductCategoriesAdapter(private val viewModel: ProductListViewModel) :
    ListAdapter<CategoriesList, ProductCategoriesAdapter.EmployeeViewHolder>(
        TaskDiffCallback()
    ) {

    var onItemClick: ((CategoriesList) -> Unit)? = null

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
        holder.binding.root.setOnClickListener {
            onItemClick?.invoke(item)
        }
        val llDivider = holder.binding.root.findViewById<LinearLayout>(R.id.llDivider)
        if (item.selected == true)
            llDivider.visibility = View.VISIBLE
        else
            llDivider.visibility = View.INVISIBLE
        holder.bind(viewModel, item)
    }

    class EmployeeViewHolder private constructor(val binding: RowProductCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: ProductListViewModel, item: CategoriesList) {
            binding.viewModels = viewModels
            binding.categoriesList = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowProductCategoriesBinding.inflate(layoutInflater, parent, false)
                return EmployeeViewHolder(
                    binding
                )
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<CategoriesList>() {
        override fun areItemsTheSame(
            oldItem: CategoriesList,
            newItem: CategoriesList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CategoriesList,
            newItem: CategoriesList
        ): Boolean {
            return oldItem == newItem
        }
    }
}
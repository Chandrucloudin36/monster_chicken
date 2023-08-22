package com.cloudin.monsterchicken.activity.dashboard.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.activity.productlist.ProductListActivity
import com.cloudin.monsterchicken.databinding.RowDashboardCategoriesBinding

class DashboardCategoriesAdapter(private val viewModel: HomeViewModel) :
    ListAdapter<CategoriesList, DashboardCategoriesAdapter.EmployeeViewHolder>(
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
        holder.binding.root.setOnClickListener {
            val landingIntent = Intent(holder.binding.root.context, ProductListActivity::class.java)
            landingIntent.putExtra("CategoryId", item.categoryId)
            landingIntent.putExtra("CategoryName", item.categoryName)
            holder.binding.root.context.startActivity(landingIntent)
        }
        holder.bind(viewModel, item)
    }

    class EmployeeViewHolder private constructor(val binding: RowDashboardCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: HomeViewModel, item: CategoriesList) {
            binding.viewModels = viewModels
            binding.categoriesList = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowDashboardCategoriesBinding.inflate(layoutInflater, parent, false)
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
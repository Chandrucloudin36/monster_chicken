package com.cloudin.monsterchicken.activity.addresslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.databinding.RowSavedAddressedBinding

class AddressListAdapter(private val viewModel: AddressListViewModel) :
    ListAdapter<AddressesList, AddressListAdapter.EmployeeViewHolder>(
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

        val cbSetDefault =
            holder.binding.root.findViewById<CheckBox>(R.id.cbSetDefault)
        val ivAddressType =
            holder.binding.root.findViewById<ImageView>(R.id.ivAddressType)
        val tvAddressType =
            holder.binding.root.findViewById<TextView>(R.id.tvAddressType)
        val tvEdit =
            holder.binding.root.findViewById<TextView>(R.id.tvEdit)
        val tvDelete =
            holder.binding.root.findViewById<TextView>(R.id.tvDelete)
        val rlAddressParent =
            holder.binding.root.findViewById<RelativeLayout>(R.id.rlAddressParent)
        val llBottomOptions =
            holder.binding.root.findViewById<LinearLayout>(R.id.llBottomOptions)

        when (item.type) {
            "home" -> {
                ivAddressType.setImageDrawable(
                    ivAddressType.context.resources.getDrawable(
                        R.drawable.ic_home_black_24dp,
                        ivAddressType.context.theme
                    )
                )
                tvAddressType.text = "Home"
            }

            "work" -> {
                ivAddressType.setImageDrawable(
                    ivAddressType.context.resources.getDrawable(
                        R.drawable.ic_work,
                        ivAddressType.context.theme
                    )
                )
                tvAddressType.text = "Work"
            }

            else -> {
                ivAddressType.setImageDrawable(
                    ivAddressType.context.resources.getDrawable(
                        R.drawable.ic_pin,
                        ivAddressType.context.theme
                    )
                )
                tvAddressType.text = item.nickName
            }
        }

        cbSetDefault.isChecked = item.defaultAddress == 1

        rlAddressParent.setOnClickListener { viewModel.setSelectedCheckBox(position) }

        if (cbSetDefault.isChecked)
            llBottomOptions.visibility = View.VISIBLE
        else
            llBottomOptions.visibility = View.GONE

        tvEdit.setOnClickListener {
            viewModel.setEditAddress(position)
        }
        tvDelete.setOnClickListener {
            viewModel.setDeleteAddress(position)
        }

        holder.bind(viewModel, item)
    }

    class EmployeeViewHolder private constructor(val binding: RowSavedAddressedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: AddressListViewModel, item: AddressesList) {
            binding.viewModels = viewModels
            binding.addressList = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowSavedAddressedBinding.inflate(layoutInflater, parent, false)
                return EmployeeViewHolder(
                    binding
                )
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<AddressesList>() {
        override fun areItemsTheSame(
            oldItem: AddressesList,
            newItem: AddressesList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AddressesList,
            newItem: AddressesList
        ): Boolean {
            return oldItem == newItem
        }
    }
}
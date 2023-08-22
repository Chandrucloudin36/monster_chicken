package com.cloudin.monsterchicken.activity.productlist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.dashboard.ui.home.ProductList
import com.cloudin.monsterchicken.activity.productdetatails.ProductDetailsActivity
import com.cloudin.monsterchicken.databinding.RowProductListBinding
import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.cloudin.monsterchicken.utils.autoimageslider.SliderAnimations
import com.cloudin.monsterchicken.utils.autoimageslider.SliderView

class ProductAdapter(private val viewModel: ProductListViewModel) :
    ListAdapter<ProductList, ProductAdapter.EmployeeViewHolder>(
        TaskDiffCallback()
    ) {

    var onItemClick: ((ProductList) -> Unit)? = null

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

        val productImageSlider =
            holder.binding.root.findViewById<SliderView>(R.id.productImageSlider)
        val tvCartCount =
            holder.binding.root.findViewById<TextView>(R.id.tvCartCount)
        val ivAddProductCount =
            holder.binding.root.findViewById<ImageView>(R.id.ivAddProductCount)
        val ivRemoveProductCount =
            holder.binding.root.findViewById<ImageView>(R.id.ivRemoveProductCount)
        val pbAddOrRemoveProduct =
            holder.binding.root.findViewById<ProgressBar>(R.id.pbAddOrRemoveProduct)

        val productsSliderListAdapter =
            ProductsSliderListAdapter(holder.binding.root.context)

        val cvAddProduct =
            holder.binding.root.findViewById<CardView>(R.id.cvAddProduct)
        val llBrandDetails =
            holder.binding.root.findViewById<LinearLayout>(R.id.llBrandDetails)
        val cvProductCount =
            holder.binding.root.findViewById<CardView>(R.id.cvProductCount)
        val cvOutOfStock =
            holder.binding.root.findViewById<CardView>(R.id.cvOutOfStock)

        val llProduct =
            holder.binding.root.findViewById<LinearLayout>(R.id.llProduct)



        if (item.showProgress == true) {
            tvCartCount.visibility = View.GONE
            pbAddOrRemoveProduct.visibility = View.VISIBLE
        } else {
            tvCartCount.visibility = View.VISIBLE
            pbAddOrRemoveProduct.visibility = View.GONE
        }


        cvOutOfStock.visibility = View.GONE

        if (item.cartProductQuantity == 0) {
            if (item.showProgress == true) {
                cvAddProduct.visibility = View.GONE
                cvProductCount.visibility = View.VISIBLE
            } else {
                cvAddProduct.visibility = View.VISIBLE
                cvProductCount.visibility = View.GONE
            }
        } else if (item.cartProductQuantity!! > 0) {
            cvAddProduct.visibility = View.GONE
            cvProductCount.visibility = View.VISIBLE
            tvCartCount.text = "" + item.cartProductQuantity!!
        } else {
            cvAddProduct.visibility = View.VISIBLE
            cvProductCount.visibility = View.GONE
        }

        if (item.brand == null)
            llBrandDetails.visibility = View.GONE
        else if (item.brand!!.brand == "")
            llBrandDetails.visibility = View.GONE
        else llBrandDetails.visibility = View.VISIBLE

        if (item.max_order_count == item.cartProductQuantity)
            ivAddProductCount.visibility = View.INVISIBLE
        else
            ivAddProductCount.visibility = View.VISIBLE

        ivRemoveProductCount.setOnClickListener { viewModel.addToCart(item, position, false) }
        cvAddProduct.setOnClickListener { viewModel.addToCart(item, position, true) }
        ivAddProductCount.setOnClickListener { viewModel.addToCart(item, position, true) }

        productsSliderListAdapter.deleteItem()
        val imageUrlList = arrayListOf<String>()
        for (imageUrl in item.imageUrl) {
            imageUrlList.add(imageUrl.file_url!!)
        }
        productsSliderListAdapter.addItem(
            imageUrlList
        )

        productImageSlider.setSliderAdapter(productsSliderListAdapter)
        productImageSlider.currentPagePosition = 0
        productImageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        productImageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)

        llProduct.setOnClickListener {
            val productIntent =
                Intent(holder.binding.root.context, ProductDetailsActivity::class.java)
            productIntent.putExtra("ProductId", item.productId)
            productIntent.putExtra("NearByBranch", item.nearByBranch)
            holder.binding.root.context.startActivity(productIntent)
        }

        holder.bind(viewModel, item)
    }

    class EmployeeViewHolder private constructor(val binding: RowProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModels: ProductListViewModel, item: ProductList) {
            binding.viewModels = viewModels
            binding.productList = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowProductListBinding.inflate(layoutInflater, parent, false)
                return EmployeeViewHolder(
                    binding
                )
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<ProductList>() {
        override fun areItemsTheSame(
            oldItem: ProductList,
            newItem: ProductList
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ProductList,
            newItem: ProductList
        ): Boolean {
            return oldItem == newItem
        }
    }
}
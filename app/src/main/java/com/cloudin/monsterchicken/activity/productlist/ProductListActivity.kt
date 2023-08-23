package com.cloudin.monsterchicken.activity.productlist

import SharedViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.cart.CartActivity
import com.cloudin.monsterchicken.activity.dashboard.ui.home.CategoriesList
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityProductListBinding
import com.cloudin.monsterchicken.utils.CART_AMOUNT
import com.cloudin.monsterchicken.utils.CART_COUNT
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.USER_LOCATION_LOCALITY

class ProductListActivity : CloudInBaseActivity() {

    private lateinit var productListBinding: ActivityProductListBinding
    private val productListViewModel: ProductListViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var productCategoriesAdapter: ProductCategoriesAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_product_list)

        productListBinding.productListViewModel = productListViewModel
        productListBinding.lifecycleOwner = this
        setContentView(productListBinding.root)

        initView()
    }

    private fun initView() {
        productListViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        productListViewModel.appLogoutLiveData.observe(this) {
            logout(this@ProductListActivity)
        }

        productListViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        if (intent != null && intent.getStringExtra("CategoryId") != null) {
            productListViewModel.selectedCategoryId.value = intent.getStringExtra("CategoryId")
            productListViewModel.selectedCategoryName.value = intent.getStringExtra("CategoryName")
        }

        productListBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        productCategoriesAdapter = ProductCategoriesAdapter(productListViewModel)
        productListBinding.rvCategoryList.adapter = productCategoriesAdapter

        productAdapter = ProductAdapter(productListViewModel)
        productListBinding.rvProductList.adapter = productAdapter


        productCategoriesAdapter.onItemClick =
            { categoriesListItemValue: CategoriesList ->
                productListViewModel.selectedCategoryId.value = categoriesListItemValue.categoryId
                productListViewModel.selectedCategoryName.value =
                    categoriesListItemValue.categoryName
                startShimmer()
                productListViewModel.getProductList()
               // productListViewModel.getCartvalue()
            }

        productListViewModel.cartCountString.observe(this) {
            if (productListViewModel.cartCount.value!! > 0) {
                sharedViewModel.setSharedValue(productListViewModel.cartCount.value.toString())

                CloudInPreferenceManager.setString(CART_COUNT, productListViewModel.cartCount.value.toString())
                CloudInPreferenceManager.setString(CART_AMOUNT, productListViewModel.cartTotalPrice.value.toString())
                productListBinding.tvCartItemCount.text =
                    "" + productListViewModel.cartCount.value + " Item  |  "
                productListBinding.tvCartItemPrice.text = productListViewModel.cartTotalPrice.value
                productListBinding.rlViewCart.visibility = View.VISIBLE
            } else
                productListBinding.rlViewCart.visibility = View.GONE

            stopShimmer()
        }

        productListViewModel.lastSelectedPosition.observe(this) {
            if (it > -1) {
                productAdapter.notifyItemChanged(it)
            }
        }

        productListBinding.rlViewCart.setOnClickListener {
            val cartIntent = Intent(this@ProductListActivity, CartActivity::class.java)
            startActivity(cartIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        startShimmer()
        productListViewModel.getProductList()
       // productListViewModel.getCartvalue()
    }

    private fun startShimmer() {
        productListBinding.slProductList.visibility = View.VISIBLE
        productListBinding.llProductList.visibility = View.GONE
        productListBinding.slProductList.startShimmer()
    }

    private fun stopShimmer() {
        productListBinding.slProductList.stopShimmer()
        productListBinding.slProductList.visibility = View.GONE
        productListBinding.llProductList.visibility = View.VISIBLE
    }
}
package com.cloudin.monsterchicken.activity.productdetatails

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.productlist.ProductsSliderListAdapter
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityProductDetailsBinding
import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.cloudin.monsterchicken.utils.autoimageslider.SliderAnimations
import com.cloudin.monsterchicken.utils.mappicker.extenstion.hide
import com.cloudin.monsterchicken.utils.mappicker.extenstion.show

class ProductDetailsActivity : CloudInBaseActivity() {

    private lateinit var productDetailsBinding: ActivityProductDetailsBinding
    private val productDetailsViewModel: ProductDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_product_details)

        productDetailsBinding.productDetailsViewModel = productDetailsViewModel
        productDetailsBinding.lifecycleOwner = this
        setContentView(productDetailsBinding.root)

        initView()
    }

    private fun initView() {
        productDetailsViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        productDetailsViewModel.appLogoutLiveData.observe(this) {
            logout(this@ProductDetailsActivity)
        }

        productDetailsViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        productDetailsViewModel.productId.value =
            intent?.extras?.getString("ProductId", "").orEmpty()

        productDetailsViewModel.nearByBranch.value =
            intent?.extras?.getString("NearByBranch", "").orEmpty()

        productDetailsBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        if (!productDetailsViewModel.productId.value.equals("") && !productDetailsViewModel.nearByBranch.value.equals(
                ""
            )
        )
            productDetailsViewModel.getProductDetails()

        val productsSliderListAdapter =
            ProductsSliderListAdapter(this@ProductDetailsActivity)

        productDetailsViewModel.productImageList.observe(this@ProductDetailsActivity) {
            if (it.isNotEmpty()) {
                productsSliderListAdapter.deleteItem()
                val imageUrlList = arrayListOf<String>()
                for (imageUrl in it) {
                    imageUrlList.add(imageUrl.file_url!!)
                }
                productsSliderListAdapter.addItem(imageUrlList)
            }
        }

        productDetailsBinding.ivRemoveProductCount.setOnClickListener {
            productDetailsViewModel.addToCart(
                false
            )
            showAddProgress()
        }
        productDetailsBinding.cvAddProduct.setOnClickListener {
            productDetailsViewModel.addToCart(
                true
            )
            showAddProgress()
        }
        productDetailsBinding.ivAddProductCount.setOnClickListener {
            productDetailsViewModel.addToCart(
                true
            )
            showAddProgress()
        }

        productDetailsViewModel.productDetails.observe(this@ProductDetailsActivity) {
            productDetailsViewModel.selectedCategoryName.value =
                it.categoryName!!.category
            productDetailsViewModel.productName.value =
                it.productName!!
            if (it.brand != null)
                productDetailsViewModel.brand.value =
                    it.brand!!.brand
            productDetailsViewModel.description.value =
                it.description!!
            productDetailsViewModel.productUnit.value =
                it.unitQty.toString()
            productDetailsViewModel.productUnitType.value =
                it.unit!!.code
            productDetailsViewModel.price.value =
                it.price!!

            if (!it.totalCartPrice.equals("0")) {
                productDetailsBinding.tvTotalPrice.visibility = View.VISIBLE
                productDetailsBinding.tvTotalPrice.text = "₹ ${it.totalCartPrice}"
            } else
                productDetailsBinding.tvTotalPrice.visibility = View.GONE

            productDetailsViewModel.productImageList.value =
                it.imageUrl


            productDetailsBinding.cvOutOfStock.visibility = View.GONE
            if (it.cartProductQuantity == 0) {
                if (it.showProgress == true) {
                    productDetailsBinding.cvAddProduct.visibility = View.GONE
                    productDetailsBinding.cvProductCount.visibility = View.VISIBLE
                } else {
                    productDetailsBinding.cvAddProduct.visibility = View.VISIBLE
                    productDetailsBinding.cvProductCount.visibility = View.GONE
                }
            } else if (it.cartProductQuantity!! > 0) {
                productDetailsBinding.cvAddProduct.visibility = View.GONE
                productDetailsBinding.cvProductCount.visibility = View.VISIBLE
                productDetailsBinding.tvCartCount.text = "" + it.cartProductQuantity!!
            } else {
                productDetailsBinding.cvAddProduct.visibility = View.VISIBLE
                productDetailsBinding.cvProductCount.visibility = View.GONE
            }
            hideAddProgress()
        }

        productDetailsViewModel.brand.observe(this@ProductDetailsActivity) {
            if (it == null || it.equals(""))
                productDetailsBinding.llBrandDetails.visibility = View.GONE
            else
                productDetailsBinding.llBrandDetails.visibility = View.VISIBLE
        }

        productDetailsViewModel.price.observe(this@ProductDetailsActivity) {
            productDetailsBinding.tvProductPrice.text = "₹ ${productDetailsViewModel.price.value}"
        }

        productDetailsBinding.productImageSlider.setSliderAdapter(productsSliderListAdapter)
        productDetailsBinding.productImageSlider.currentPagePosition = 0
        productDetailsBinding.productImageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        productDetailsBinding.productImageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)

    }

    private fun showAddProgress() {
        productDetailsBinding.pbAddOrRemoveProduct.show()
        productDetailsBinding.tvCartCount.visibility = View.GONE
    }

    private fun hideAddProgress() {
        productDetailsBinding.pbAddOrRemoveProduct.hide()
        productDetailsBinding.tvCartCount.visibility = View.VISIBLE
    }
}
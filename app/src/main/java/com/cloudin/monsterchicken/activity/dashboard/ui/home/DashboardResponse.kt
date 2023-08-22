package com.cloudin.monsterchicken.activity.dashboard.ui.home

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DashboardResponse(
    @SerializedName("message")
    @Expose
    var message: List<String>? = null,

    @SerializedName("response")
    @Expose
    var response: Response? = null,

    @SerializedName("status")
    @Expose
    var status: Boolean = false
) : Parcelable

@Parcelize
data class Response(
    @SerializedName("categories")
    @Expose
    var categories: List<CategoriesList> = arrayListOf(),
    @SerializedName("banners")
    @Expose
    var banners: List<String> = arrayListOf(),
    @SerializedName("cartCount")
    @Expose
    var cartCount: Int = 0,
    @SerializedName("unique_token")
    @Expose
    var unique_token: String = "",
    @SerializedName("totalCartPrice")
    @Expose
    var totalCartPrice: String = "",
    @SerializedName("cart")
    @Expose
    var cart_details: CartDetails,
    @SerializedName("products")
    @Expose
    var products: List<ProductList> = arrayListOf()
) : Parcelable

@Parcelize
data class BannersList(
    @SerializedName("image")
    @Expose
    var image: String? = null,
) : Parcelable

@Parcelize
data class CategoriesList(
    @SerializedName("category")
    @Expose
    var categoryName: String? = null,
    @SerializedName("selected")
    @Expose
    var selected: Boolean? = null,
    @SerializedName("product_category_id")
    @Expose
    var categoryId: String? = null,
    @SerializedName("image_url")
    @Expose
    var imageUrl: List<ImageUrl> = arrayListOf()
) : Parcelable

@Parcelize
data class ImageUrl(
    @SerializedName("file_url")
    @Expose
    var file_url: String? = null,
) : Parcelable

@Parcelize
data class CartDetails(
    @SerializedName("cart_count")
    @Expose
    var cart_count: Int,
    @SerializedName("cart_total_amount")
    @Expose
    var cart_total_amount: String,
    @SerializedName("cart")
    @Expose
    var cart_lists: List<CartLists> = arrayListOf()
) : Parcelable

@Parcelize
data class CartLists(
    @SerializedName("cart_id")
    @Expose
    var cart_id: String,
    @SerializedName("product")
    @Expose
    var productDetails: ProductList,
) : Parcelable

@Parcelize
data class ProductList(
    @SerializedName("name")
    @Expose
    var productName: String? = null,
    @SerializedName("product_id")
    @Expose
    var productId: String? = null,
    @SerializedName("sale_price")
    @Expose
    var price: String? = null,
    @SerializedName("total_cart_price")
    @Expose
    var totalCartPrice: String? = null,
    @SerializedName("product_category")
    @Expose
    var categoryName: ProductCategory? = null,
    @SerializedName("mc_unit")
    @Expose
    var unit: McUnit? = null,
    @SerializedName("productUnit")
    @Expose
    var productUnit: String? = null,
    @SerializedName("productUnitType")
    @Expose
    var productUnitType: String? = null,
    @SerializedName("brand")
    @Expose
    var brand: Brand? = null,
    @SerializedName("near_by_branch")
    @Expose
    var nearByBranch: String? = "",
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("image_url")
    @Expose
    var imageUrl: List<ImageUrl> = arrayListOf(),
    var showProgress: Boolean? = false,
    @SerializedName("cart_product_quantity")
    @Expose
    var cartProductQuantity: Int? = 0,
    @SerializedName("stock")
    @Expose
    var stock: Int? = 0,
    @SerializedName("unit")
    @Expose
    var unitQty: Double? = 0.0,
    @SerializedName("max_order_count")
    @Expose
    var max_order_count: Int? = 0,
) : Parcelable

@Parcelize
data class Brand(
    @SerializedName("brand")
    @Expose
    var brand: String,
) : Parcelable

@Parcelize
data class McUnit(
    @SerializedName("code")
    @Expose
    var code: String,
) : Parcelable

@Parcelize
data class ProductCategory(
    @SerializedName("category")
    @Expose
    var category: String,
) : Parcelable

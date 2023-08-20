package com.cloudin.monsterchicken.commonclass

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.cloudin.monsterchicken.activity.addresslist.AddressListAdapter
import com.cloudin.monsterchicken.activity.addresslist.AddressesList
import com.cloudin.monsterchicken.activity.cart.CartDetails
import com.cloudin.monsterchicken.activity.cart.CartListAdapter
import com.cloudin.monsterchicken.activity.checkout.CheckoutCartListAdapter
import com.cloudin.monsterchicken.activity.dashboard.ui.home.CategoriesList
import com.cloudin.monsterchicken.activity.dashboard.ui.home.DashboardCategoriesAdapter
import com.cloudin.monsterchicken.activity.dashboard.ui.home.ProductList
import com.cloudin.monsterchicken.activity.notificationlist.NotificationListAdapter
import com.cloudin.monsterchicken.activity.notificationlist.Notifications
import com.cloudin.monsterchicken.activity.orders.OrderList
import com.cloudin.monsterchicken.activity.orders.OrderListAdapter
import com.cloudin.monsterchicken.activity.orders.OrderListItemAdapter
import com.cloudin.monsterchicken.activity.orders.ProductsItem
import com.cloudin.monsterchicken.activity.productlist.ProductAdapter
import com.cloudin.monsterchicken.activity.productlist.ProductCategoriesAdapter

@BindingAdapter("loadImage")
fun setLoadImage(imageView: ImageView, imgUrl: String) {
    if (imgUrl != null) {
        if (imgUrl.endsWith("svg")) {
            val imageLoader = ImageLoader.Builder(imageView.context)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            val request = ImageRequest.Builder(imageView.context)
                .crossfade(true)
                .crossfade(500)
                .data(imgUrl)
                .target(imageView)
                .build()

            imageLoader.enqueue(request)
        } else
            Glide.with(imageView.context).load(imgUrl)
                .into(imageView)
    }
}

@BindingAdapter("app:dashboardCategoriesList")
fun setDashboardCategoriesValues(
    listView: RecyclerView,
    items: List<CategoriesList>?
) {
    items?.let {
        (listView.adapter as DashboardCategoriesAdapter).submitList(items)
    }
}

@BindingAdapter("app:productCategoryList")
fun setProductCategoryValues(
    listView: RecyclerView,
    items: List<CategoriesList>?
) {
    items?.let {
        (listView.adapter as ProductCategoriesAdapter).submitList(items)
    }
}

@BindingAdapter("app:productsList")
fun setProductsListValues(
    listView: RecyclerView,
    items: List<ProductList>?
) {
    items?.let {
        (listView.adapter as ProductAdapter).submitList(items)
    }
}

@BindingAdapter("app:cartItemList")
fun setCartItemListValues(
    listView: RecyclerView,
    items: List<CartDetails>?
) {
    items?.let {
        (listView.adapter as CartListAdapter).submitList(items)
    }
}

@BindingAdapter("app:cartCheckoutItemList")
fun setCartCheckoutItemListValues(
    listView: RecyclerView,
    items: List<com.cloudin.monsterchicken.activity.checkout.CartDetails>?
) {
    items?.let {
        (listView.adapter as CheckoutCartListAdapter).submitList(items)
    }
}

@BindingAdapter("app:addressItemList")
fun setAddressItemListValues(
    listView: RecyclerView,
    items: List<AddressesList>?
) {
    items?.let {
        (listView.adapter as AddressListAdapter).submitList(items)
    }
}

@BindingAdapter("app:ordersList")
fun setOrdersItemListValues(
    listView: RecyclerView,
    items: List<OrderList>?
) {
    items?.let {
        (listView.adapter as OrderListAdapter).submitList(items)
    }
}

@BindingAdapter("app:NotificationList")
fun setNotificationsItemListValues(
    listView: RecyclerView,
    items: List<Notifications>?
) {
    items?.let {
        (listView.adapter as NotificationListAdapter).submitList(items)
    }
}

@BindingAdapter("app:ordersProductList")
fun setOrdersProductItemListValues(
    listView: RecyclerView,
    items: List<ProductsItem>?
) {
    items?.let {
        (listView.adapter as OrderListItemAdapter).submitList(items)
    }
}
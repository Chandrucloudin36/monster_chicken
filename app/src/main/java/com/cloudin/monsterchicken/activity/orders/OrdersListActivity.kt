package com.cloudin.monsterchicken.activity.orders

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityOrdersListBinding

class OrdersListActivity : CloudInBaseActivity() {

    private lateinit var ordersListBinding: ActivityOrdersListBinding
    private val ordersListViewModel: OrdersListViewModel by viewModels()
    private lateinit var orderListAdapter: OrderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ordersListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_orders_list)

        ordersListBinding.ordersViewModel = ordersListViewModel
        ordersListBinding.lifecycleOwner = this
        setContentView(ordersListBinding.root)

        initView()
    }

    private fun initView() {
        ordersListViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        ordersListViewModel.appLogoutLiveData.observe(this) {
            logout(this@OrdersListActivity)
        }

        ordersListViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        ordersListBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        orderListAdapter = OrderListAdapter(ordersListViewModel)
        ordersListBinding.rvOrderList.adapter = orderListAdapter

        ordersListViewModel.getOrderList()
        ordersListViewModel.downloadLink.observe(this) {
            if (it.status) {
                val pdfName = "Monster_" + ordersListViewModel.orderId1 + ".pdf"
                downloadTask(it.response!!.download_url, pdfName)
            }
        }
    }
}
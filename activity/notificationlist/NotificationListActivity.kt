package com.cloudin.monsterchicken.activity.notificationlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityNotificationListBinding

class NotificationListActivity : CloudInBaseActivity() {

    private lateinit var notificationListBinding: ActivityNotificationListBinding
    private val notificationListViewModel: NotificationListViewModel by viewModels()
    private lateinit var notificationListAdapter: NotificationListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_notification_list)

        notificationListBinding.notificationListViewModel = notificationListViewModel
        notificationListBinding.lifecycleOwner = this
        setContentView(notificationListBinding.root)

        initView()
    }

    private fun initView() {
        notificationListViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        notificationListViewModel.appLogoutLiveData.observe(this) {
            logout(this@NotificationListActivity)
        }

        notificationListViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        notificationListBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        notificationListAdapter = NotificationListAdapter(notificationListViewModel)
        notificationListBinding.rvNotificationList.adapter = notificationListAdapter

        notificationListViewModel.getNotificationList()

        notificationListViewModel.lastSelectedId.observe(this@NotificationListActivity) {
            if (it > -1) {
                notificationListViewModel.notificationListValues.value!![it].is_read = 1
                notificationListAdapter.notifyItemChanged(it)
                Toast.makeText(
                    this@NotificationListActivity,
                    getString(R.string.profile_fragment_notification_updated_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
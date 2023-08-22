package com.cloudin.monsterchicken

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityMainBinding

class MainActivity : CloudInBaseActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainBinding.mainViewModel = mainViewModel
        mainBinding.lifecycleOwner = this
        setContentView(mainBinding.root)

        initView()
    }

    private fun initView() {
        mainViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        mainViewModel.appLogoutLiveData.observe(this) {
            logout(this@MainActivity)
        }

        mainViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        mainViewModel.getDashboard()
    }
}
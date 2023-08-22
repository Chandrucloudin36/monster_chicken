package com.cloudin.monsterchicken.activity.addresslist

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.addaddress.AddAddressActivity
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.databinding.ActivityAddressListBinding
import com.cloudin.monsterchicken.utils.NativeDialogClickListener
import com.cloudin.monsterchicken.utils.NativeUtils

class AddressListActivity : AppCompatActivity() {

    private lateinit var addressListBinding: ActivityAddressListBinding
    private val addAddressViewModel: AddressListViewModel by viewModels()
    private lateinit var addressListAdapter: AddressListAdapter
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val openCheckoutIntent =
                        Intent(this@AddressListActivity, AddAddressActivity::class.java)
                    startActivity(openCheckoutIntent)
                }
            }
        }
    private val startForEditResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val openCheckoutIntent =
                        Intent(this@AddressListActivity, AddAddressActivity::class.java)
                    startActivity(openCheckoutIntent)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addressListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_address_list)

        addressListBinding.addressListViewModel = addAddressViewModel
        addressListBinding.lifecycleOwner = this
        setContentView(addressListBinding.root)

        initView()
    }

    private fun initView() {
        addAddressViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        addAddressViewModel.appLogoutLiveData.observe(this) {
            logout(this@AddressListActivity)
        }

        addAddressViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        addressListBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        addressListAdapter = AddressListAdapter(addAddressViewModel)
        addressListBinding.rvSavedAddressesList.adapter = addressListAdapter

        addAddressViewModel.lastSelectedPosition.observe(this) {
            if (it > -1) {
                for ((arrayPos, cartItems) in addAddressViewModel.addressesListValues.value!!.withIndex()) {
                    if (arrayPos == it)
                        cartItems.defaultAddress = 1
                    else
                        cartItems.defaultAddress = 0
                }
                addressListAdapter.notifyDataSetChanged()
                checkProceedAvailability()
            }
        }

        addAddressViewModel.editAddressPosition.observe(this) {
            if (it > -1) {
                val addAddressIntent =
                    Intent(this@AddressListActivity, AddAddressActivity::class.java)
                addAddressIntent.putExtra(
                    "ADDRESS_DETAILS",
                    addAddressViewModel.addressesListValues.value!![addAddressViewModel.editAddressPosition.value!!]
                )
                startForResult.launch(addAddressIntent)
            }
        }

        addAddressViewModel.deletedPosition.observe(this) {
            if (it > -1) {
                NativeUtils.showConfirmAlert(
                    this@AddressListActivity,
                    getString(R.string.delete_message),
                    true,
                    getString(R.string.confirm),
                    getString(R.string.cancel),
                    object : NativeDialogClickListener {
                        override fun onPositive(dialog: DialogInterface?) {
                            addAddressViewModel.setCallDeleteAddress(addAddressViewModel.addressesListValues.value!![it].id)
                        }

                        override fun onNegative(dialog: DialogInterface?) {
                            dialog?.dismiss()
                        }
                    }
                )
            }
        }

        addressListBinding.tvAddNewAddress.setOnClickListener {
            val addAddressIntent = Intent(this@AddressListActivity, AddAddressActivity::class.java)
            startForResult.launch(addAddressIntent)
        }

        addressListBinding.tvProceed.setOnClickListener {
            var addressId = ""
            for (cartItems in addAddressViewModel.addressesListValues.value!!) {
                if (cartItems.defaultAddress == 1) {
                    addressId = cartItems.id
                    break
                }
            }
            addAddressViewModel.setAddressAsDefault(addressId)
        }

        addAddressViewModel.setDefaultAddress.observe(this@AddressListActivity) {
            if (it) {
                addAddressViewModel.setDefaultAddress.value = false
                finish()
            }
        }
        addAddressViewModel.deletedAddress.observe(this@AddressListActivity) {
            if (it) {
                addAddressViewModel.deletedAddress.value = false
                addAddressViewModel.addressesListValues.value!!.removeAt(addAddressViewModel.deletedPosition.value!!)
                addressListAdapter.notifyItemRemoved(addAddressViewModel.deletedPosition.value!!)
                checkProceedAvailability()
            }
        }
        checkProceedAvailability()
    }

    private fun checkProceedAvailability() {
        var addressId = ""
        if (addAddressViewModel.addressesListValues.value != null)
            for (cartItems in addAddressViewModel.addressesListValues.value!!) {
                if (cartItems.defaultAddress == 1) {
                    addressId = cartItems.id
                    break
                }
            }
        if (addressId == "")
            addressListBinding.tvProceed.visibility = View.GONE
        else
            addressListBinding.tvProceed.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        addAddressViewModel.getAddressList()
    }
}
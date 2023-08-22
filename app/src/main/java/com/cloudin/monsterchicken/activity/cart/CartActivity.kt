package com.cloudin.monsterchicken.activity.cart

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.checkout.CheckoutActivity
import com.cloudin.monsterchicken.activity.verifyotp.VerifyOTPActivity
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityCartBinding
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.USER_AUTH_TOKEN
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText

class CartActivity : CloudInBaseActivity() {

    private lateinit var cartBinding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var cartListAdapter: CartListAdapter
    private var dialogBsd: BottomSheetDialog? = null
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                dialogBsd!!.dismiss()
                if (intent != null) {
                    val openCheckoutIntent =
                        Intent(this@CartActivity, CheckoutActivity::class.java)
                    startActivity(openCheckoutIntent)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_cart)

        cartBinding.cartViewModel = cartViewModel
        cartBinding.lifecycleOwner = this
        setContentView(cartBinding.root)

        initView()
    }

    private fun initView() {
        cartViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        cartViewModel.appLogoutLiveData.observe(this) {
            logout(this@CartActivity)
        }

        cartViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }
        cartBinding.ivBackIcon.setOnClickListener { onBackPressed() }
        cartListAdapter = CartListAdapter(cartViewModel)
        cartBinding.rvCartList.adapter = cartListAdapter

        cartViewModel.lastSelectedPosition.observe(this) {
            if (it > -1) {
                if (cartViewModel.removeItemPosition.value!!)
                    cartListAdapter.notifyItemRemoved(it)
                else
                    cartListAdapter.notifyItemChanged(it)
            }
        }
        showShimmer()
        cartViewModel.getCartList()

        cartBinding.llTotalPrice.setOnClickListener {
            cartBinding.nsvCart.smoothScrollTo(0, cartBinding.nsvCart.getChildAt(0).height)
        }

        cartViewModel.cartListValues.observe(this@CartActivity) {
            hideShimmer()
        }

        cartBinding.llCheckout.setOnClickListener {
            if (CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "").equals(""))
                showLoginBottomSheet()
            else {
                openCheckoutActivity()
            }
        }

        cartViewModel.otpReceived.observe(this) {
            if (it) {
                dialogBsd!!.hide()
                cartViewModel.otpReceived.value = false
                openValidateOtpActivity()
            }
        }
        cartViewModel.cartListIsEmpty.observe(this) {
            if (!it)
                cartBinding.llCheckout.visibility = View.INVISIBLE
            else
                cartBinding.llCheckout.visibility = View.VISIBLE
        }
    }

    private fun openCheckoutActivity() {
        val openCheckoutIntent =
            Intent(this@CartActivity, CheckoutActivity::class.java)
        startActivity(openCheckoutIntent)
    }

    private fun showLoginBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_login, null)
        dialogBsd = BottomSheetDialog(this)
        dialogBsd!!.setContentView(view)
        dialogBsd!!.setCancelable(true)
        dialogBsd!!.setCanceledOnTouchOutside(true)
        val bottomSheet = dialogBsd!!.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )
            ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)

        val tetPhoneNumber = dialogBsd!!.findViewById<TextInputEditText>(R.id.tetPhoneNumber)
        val tvProceedWithOtp = dialogBsd!!.findViewById<TextView>(R.id.tvProceedWithOtp)
        tetPhoneNumber!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 10) {
                    if (tvProceedWithOtp != null) {
                        tvProceedWithOtp.isEnabled = true
                        tvProceedWithOtp.background =
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.common_botton,
                                theme
                            )
                        cartViewModel.phoneNumber.value = s.toString()
                    }
                } else {
                    if (tvProceedWithOtp != null) {
                        tvProceedWithOtp.isEnabled = false
                        tvProceedWithOtp.background =
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.common_button_un_selected,
                                theme
                            )

                        cartViewModel.phoneNumber.value = ""
                    }

                }

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        tvProceedWithOtp!!.setOnClickListener {
            if (tvProceedWithOtp.isEnabled) {
                cartViewModel.getRequestOtp()
            }
//            openValidateOtpActivity()
        }

        dialogBsd!!.show()
    }

    private fun showShimmer() {
        cartBinding.slCartList.startShimmer()
        cartBinding.slCartList.visibility = View.VISIBLE
        cartBinding.rlCartListParent.visibility = View.GONE
    }

    private fun hideShimmer() {
        cartBinding.slCartList.stopShimmer()
        cartBinding.slCartList.visibility = View.GONE
        cartBinding.rlCartListParent.visibility = View.VISIBLE
    }

    private fun openValidateOtpActivity() {
        val verifyOtpActivityIntent = Intent(this@CartActivity, VerifyOTPActivity::class.java)
        verifyOtpActivityIntent.putExtra("PhoneNumber", cartViewModel.phoneNumber.value)
        startForResult.launch(verifyOtpActivityIntent)
    }
}
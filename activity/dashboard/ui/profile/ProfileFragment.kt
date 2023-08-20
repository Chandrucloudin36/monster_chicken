package com.cloudin.monsterchicken.activity.dashboard.ui.profile

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.cloudin.monsterchicken.BuildConfig
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.addresslist.AddressListActivity
import com.cloudin.monsterchicken.activity.dashboard.DashboardActivity
import com.cloudin.monsterchicken.activity.notificationlist.NotificationListActivity
import com.cloudin.monsterchicken.activity.orders.OrdersListActivity
import com.cloudin.monsterchicken.activity.profiledetails.ProfileDetailsActivity
import com.cloudin.monsterchicken.activity.verifyotp.VerifyOTPActivity
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInFragment
import com.cloudin.monsterchicken.databinding.FragmentProfileBinding
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.Constant
import com.cloudin.monsterchicken.utils.NativeDialogClickListener
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.USER_AUTH_TOKEN
import com.cloudin.monsterchicken.utils.USER_EMAIL
import com.cloudin.monsterchicken.utils.USER_NAME
import com.cloudin.monsterchicken.utils.USER_PHONE_NUMBER
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText

class ProfileFragment : CloudInFragment() {

    private lateinit var fragmentProfileBinding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var dashboardActivity: DashboardActivity
    private var dialogBsd: BottomSheetDialog? = null
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (dialogBsd != null)
                    dialogBsd!!.dismiss()
                checkUserLogin()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false).apply {
                this.profileViewModels = profileViewModel
            }
        return fragmentProfileBinding.root
    }

    override fun onAttach(dashboardActivity: Activity) {
        super.onAttach(dashboardActivity)
        this.dashboardActivity = dashboardActivity as DashboardActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentProfileBinding.lifecycleOwner = this.viewLifecycleOwner
        initView()
    }

    private fun initView() {
        profileViewModel.errorListLiveData.observe(dashboardActivity) {
            dashboardActivity.errorDialog(it, dashboardActivity)
        }

        profileViewModel.appLogoutLiveData.observe(dashboardActivity) {
            dashboardActivity.logout(dashboardActivity)
        }

        profileViewModel.loaderFlagLiveData.observe(dashboardActivity) {
            dashboardActivity.showLoader(it)
        }

        fragmentProfileBinding.tvLoginSignup.setOnClickListener {
            showLoginBottomSheet()
        }

        profileViewModel.otpReceived.observe(dashboardActivity) {
            if (it) {
                if (dialogBsd != null) {
                    dialogBsd!!.hide()
                    profileViewModel.otpReceived.value = false
                    openValidateOtpActivity()
                }
            }
        }

        fragmentProfileBinding.llAddresses.setOnClickListener {
            val addressListIntent =
                Intent(dashboardActivity, AddressListActivity::class.java)
            startActivity(addressListIntent)
        }

        fragmentProfileBinding.llOrdersList.setOnClickListener {
            val addressListIntent =
                Intent(dashboardActivity, OrdersListActivity::class.java)
            startActivity(addressListIntent)
        }

        fragmentProfileBinding.llNotificationList.setOnClickListener {
            val notificationListIntent =
                Intent(dashboardActivity, NotificationListActivity::class.java)
            startActivity(notificationListIntent)
        }

        fragmentProfileBinding.llLogout.setOnClickListener {
            callLogout()
        }

        fragmentProfileBinding.tvEditProfile.setOnClickListener {
            openProfileActivity()
        }

        var appVersionNumber = "V " + Constant.flavour + "-" + BuildConfig.VERSION_NAME

        checkUserLogin()
    }

    private fun openProfileActivity() {
        val profileIntent = Intent(dashboardActivity, ProfileDetailsActivity::class.java)
        startForResult.launch(profileIntent)
//        startActivity(profileIntent)
    }

    private fun callLogout() {
        NativeUtils.showConfirmAlert(
            dashboardActivity,
            getString(R.string.log_out_message),
            true,
            getString(R.string.confirm),
            getString(R.string.cancel),
            object : NativeDialogClickListener {
                override fun onPositive(dialog: DialogInterface?) {
                    profileViewModel.callLogout()
                }

                override fun onNegative(dialog: DialogInterface?) {
                    dialog?.dismiss()
                }
            }
        )
    }

    private fun checkUserLogin() {
        if (!CloudInPreferenceManager.getString(USER_AUTH_TOKEN, "").equals("")) {
            fragmentProfileBinding.svProfileParent.visibility = View.VISIBLE
            fragmentProfileBinding.llLogin.visibility = View.GONE

            if (!CloudInPreferenceManager.getString(USER_PHONE_NUMBER, "").equals("")) {
                fragmentProfileBinding.tvMobileNumber.text = CloudInPreferenceManager.getString(
                    USER_PHONE_NUMBER,
                    ""
                )

                if (CloudInPreferenceManager.getString(USER_EMAIL, "").equals("")) {
                    fragmentProfileBinding.tvEmailAddress.text =
                        CloudInPreferenceManager.getString(
                            USER_PHONE_NUMBER,
                            ""
                        ) + "@" + dashboardActivity.getString(R.string.profile_fragment_guest)
                } else {
                    fragmentProfileBinding.tvEmailAddress.text =
                        CloudInPreferenceManager.getString(USER_EMAIL, "")

                    fragmentProfileBinding.tvUserName.text =
                        CloudInPreferenceManager.getString(USER_NAME, "")
                }
            }

        } else {
            fragmentProfileBinding.svProfileParent.visibility = View.GONE
            fragmentProfileBinding.llLogin.visibility = View.VISIBLE
        }
    }

    private fun showLoginBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_login, null)
        dialogBsd = BottomSheetDialog(dashboardActivity)
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
                                dashboardActivity.theme
                            )
                        profileViewModel.phoneNumber.value = s.toString()
                    }
                } else {
                    if (tvProceedWithOtp != null) {
                        tvProceedWithOtp.isEnabled = false
                        tvProceedWithOtp.background =
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.common_button_un_selected,
                                dashboardActivity.theme
                            )

                        profileViewModel.phoneNumber.value = ""
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
                profileViewModel.getRequestOtp()
            }
        }

        dialogBsd!!.show()
    }

    private fun openValidateOtpActivity() {
        val verifyOtpActivityIntent = Intent(dashboardActivity, VerifyOTPActivity::class.java)
        verifyOtpActivityIntent.putExtra("PhoneNumber", profileViewModel.phoneNumber.value)
        startForResult.launch(verifyOtpActivityIntent)
    }
}
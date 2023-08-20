package com.cloudin.monsterchicken.activity.profiledetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityProfileDetailsBinding
import com.cloudin.monsterchicken.utils.NativeUtils
import com.cloudin.monsterchicken.utils.imagepicker.ImagePicker
import com.cloudin.monsterchicken.utils.mappicker.utils.ToastUtils

class ProfileDetailsActivity : CloudInBaseActivity() {

    private lateinit var profileDetailsBinding: ActivityProfileDetailsBinding
    private val profileDetailsViewModel: ProfileDetailsViewModel by viewModels()
    private val PROFILE_IMAGE_REQ_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile_details)

        profileDetailsBinding.profileDetailsViewModel = profileDetailsViewModel
        profileDetailsBinding.lifecycleOwner = this
        setContentView(profileDetailsBinding.root)

        initView()
    }

    private fun initView() {
        profileDetailsViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        profileDetailsViewModel.appLogoutLiveData.observe(this) {
            logout(this@ProfileDetailsActivity)
        }

        profileDetailsViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        profileDetailsBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        profileDetailsBinding.llMale.setOnClickListener { setMaleSelected() }
        profileDetailsBinding.llFeMale.setOnClickListener { setFeMaleSelected() }
        profileDetailsBinding.llOther.setOnClickListener { setOtherSelected() }

        profileDetailsViewModel.getProfileDetails()

        profileDetailsViewModel.gender.observe(this@ProfileDetailsActivity) {
            when (it) {
                "1" -> setMaleSelected()
                "2" -> setFeMaleSelected()
                else -> setOtherSelected()
            }
            profileDetailsViewModel.genderSelected.value = it
        }

        profileDetailsViewModel.isProfileUpdated.observe(this@ProfileDetailsActivity) {
            if (it) {
                ToastUtils.showToast(
                    this@ProfileDetailsActivity,
                    getString(R.string.update_profile_details_updated_successfully)
                )
                val updateProfileDetailsIntent = Intent()
                setResult(Activity.RESULT_OK, updateProfileDetailsIntent)
                finish()
            }
        }

        profileDetailsBinding.tvUpdateProfile.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .setImageProviderInterceptor { imageProvider ->
                    Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
                }
                .setDismissListener {
                    Log.d("ImagePicker", "Dialog Dismiss")
                }
                // Image resolution will be less than 512 x 512
                .maxResultSize(512, 512)
                .start(PROFILE_IMAGE_REQ_CODE)
        }

        profileDetailsBinding.tvUpdateButton.setOnClickListener {
            if (profileDetailsViewModel.userName.value.equals("")) {
                ToastUtils.showToast(
                    this,
                    getString(R.string.update_profile_enter_name)
                )
            } else if (profileDetailsViewModel.userEmail.value.equals("")) {
                ToastUtils.showToast(
                    this,
                    getString(R.string.update_profile_enter_email)
                )
            } else {
                profileDetailsViewModel.updateProfileDetails()
            }
        }

        profileDetailsViewModel.previewUrl.observe(this@ProfileDetailsActivity) {
            if (!it.equals("")) {
                NativeUtils.setLoadImage(profileDetailsBinding.ivProfileImage, it)
            }
        }
    }

    private fun setMaleSelected() {
        profileDetailsBinding.llMale.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock_selected, theme)
        profileDetailsBinding.llFeMale.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        profileDetailsBinding.llOther.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        profileDetailsViewModel.genderSelected.value = "1"
    }

    private fun setFeMaleSelected() {
        profileDetailsBinding.llMale.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        profileDetailsBinding.llFeMale.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock_selected, theme)
        profileDetailsBinding.llOther.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        profileDetailsViewModel.genderSelected.value = "2"
    }

    private fun setOtherSelected() {
        profileDetailsBinding.llMale.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        profileDetailsBinding.llFeMale.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        profileDetailsBinding.llOther.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock_selected, theme)
        profileDetailsViewModel.genderSelected.value = "3"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    PROFILE_IMAGE_REQ_CODE -> {
                        val filePath: String = ImagePicker.getFilePath(data)!!
                        profileDetailsViewModel.updateProfileImage(filePath)
                    }
                }
            }

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(
                    this@ProfileDetailsActivity,
                    ImagePicker.getError(data),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            else -> {
                Toast.makeText(this@ProfileDetailsActivity, "Task Cancelled", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
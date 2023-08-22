package com.cloudin.monsterchicken.activity.landing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.dashboard.DashboardActivity
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityLandingBinding
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.USER_LAT
import com.cloudin.monsterchicken.utils.USER_LOCATION_LOCALITY
import com.cloudin.monsterchicken.utils.USER_LOCATION_STRING
import com.cloudin.monsterchicken.utils.USER_LONG
import com.cloudin.monsterchicken.utils.mappicker.presentation.builder.CloudinPlacePicker
import com.cloudin.monsterchicken.utils.mappicker.utils.MapType
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerLanguage
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerType

class LandingActivity : CloudInBaseActivity() {

    private lateinit var landingBinding: ActivityLandingBinding
    private val landingViewModel: LandingViewModel by viewModels()

    private var placePickerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val vanillaAddress = CloudinPlacePicker.getPlaceResult(result.data)
                vanillaAddress?.let {
                    CloudInPreferenceManager.setString(USER_LOCATION_LOCALITY, it.locality)
                    CloudInPreferenceManager.setString(USER_LOCATION_STRING, it.formattedAddress)
                    CloudInPreferenceManager.setString(USER_LAT, it.latitude!!.toString())
                    CloudInPreferenceManager.setString(USER_LONG, it.longitude!!.toString())

                    openDashboardActivity()
                }
            }
        }

    private fun openDashboardActivity() {
        val landingIntent = Intent(this@LandingActivity, DashboardActivity::class.java)
        startActivity(landingIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        landingBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_landing)

        landingBinding.landingViewModel = landingViewModel
        landingBinding.lifecycleOwner = this
        setContentView(landingBinding.root)

        initView()
    }

    private fun initView() {
        landingViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        landingViewModel.appLogoutLiveData.observe(this) {
            logout(this@LandingActivity)
        }

        landingViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        generatePushToken()

        landingBinding.tvSetLocation.setOnClickListener {
            val intent = CloudinPlacePicker.Builder(this)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE)
                .setMapType(MapType.NORMAL)
//            .withLocation(23.0710, 72.5181)
                .setCountry("IN")
                .setPickerLanguage(PickerLanguage.ENGLISH)
                .enableShowMapAfterSearchResult(true)
                .build()
            placePickerResultLauncher.launch(intent)
        }
    }
}
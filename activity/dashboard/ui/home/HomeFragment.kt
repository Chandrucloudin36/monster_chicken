package com.cloudin.monsterchicken.activity.dashboard.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.cloudin.monsterchicken.activity.dashboard.DashboardActivity
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInFragment
import com.cloudin.monsterchicken.databinding.FragmentHomeBinding
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.USER_LAT
import com.cloudin.monsterchicken.utils.USER_LOCATION_LOCALITY
import com.cloudin.monsterchicken.utils.USER_LOCATION_STRING
import com.cloudin.monsterchicken.utils.USER_LONG
import com.cloudin.monsterchicken.utils.USER_UNIQUE_TOKEN
import com.cloudin.monsterchicken.utils.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.cloudin.monsterchicken.utils.autoimageslider.SliderAnimations
import com.cloudin.monsterchicken.utils.mappicker.presentation.builder.CloudinPlacePicker
import com.cloudin.monsterchicken.utils.mappicker.utils.MapType
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerLanguage
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerType

class HomeFragment : CloudInFragment() {

    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var dashboardActivity: DashboardActivity
    private lateinit var dashboardCategoriesAdapter: DashboardCategoriesAdapter

    private var placePickerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val vanillaAddress = CloudinPlacePicker.getPlaceResult(result.data)
                vanillaAddress?.let {
                    CloudInPreferenceManager.setString(USER_LOCATION_LOCALITY, it.locality)
                    CloudInPreferenceManager.setString(USER_LOCATION_STRING, it.formattedAddress)
                    CloudInPreferenceManager.setString(USER_LAT, it.latitude!!.toString())
                    CloudInPreferenceManager.setString(
                        USER_LONG,
                        it.longitude!!.toString()
                    )

                    fragmentHomeBinding.tvSavedCity.text = CloudInPreferenceManager.getString(
                        USER_LOCATION_LOCALITY,
                        ""
                    )
                    fragmentHomeBinding.tvSavedAddress.text = CloudInPreferenceManager.getString(
                        USER_LOCATION_STRING,
                        ""
                    )
                    homeViewModel.getDashboard()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding =
            FragmentHomeBinding.inflate(inflater, container, false).apply {
                this.homeViewModels = homeViewModel
            }
        return fragmentHomeBinding.root
    }

    override fun onAttach(dashboardActivity: Activity) {
        super.onAttach(dashboardActivity)
//        homeViewModel.bannersList.value!!.add("https://api.monsterchicken.cloudinworks.com/storage/document/qxlJCp6P74LNSn57FT97rgcOFw07HI1G1vdond4N.jpg")
//        homeViewModel.bannersList.value!!.add("https://api.monsterchicken.cloudinworks.com/storage/document/EIFOkVhrHqJ0KeOD4spIrYHXMPMr4dTIAcWDLW27.jpg")
//        homeViewModel.bannersList.value!!.add("https://api.monsterchicken.cloudinworks.com/storage/document/sAfgzu3OalKZsPx0xAIhaB3Ai2A7jc5FPj15bjjt.jpg")
        this.dashboardActivity = dashboardActivity as DashboardActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentHomeBinding.lifecycleOwner = this.viewLifecycleOwner
        initView()
    }

    private fun initView() {
        homeViewModel.errorListLiveData.observe(dashboardActivity) {
            dashboardActivity.errorDialog(it, dashboardActivity)
        }

        homeViewModel.appLogoutLiveData.observe(dashboardActivity) {
            dashboardActivity.logout(dashboardActivity)
        }

        homeViewModel.loaderFlagLiveData.observe(dashboardActivity) {
            dashboardActivity.showLoader(it)
        }

        dashboardCategoriesAdapter = DashboardCategoriesAdapter(homeViewModel)
        fragmentHomeBinding.rvCategoriesList.adapter = dashboardCategoriesAdapter

        val dashboardBannerSliderListAdapter =
            DashboardBannerSliderListAdapter(dashboardActivity)

        fragmentHomeBinding.tvSavedCity.text = CloudInPreferenceManager.getString(
            USER_LOCATION_LOCALITY,
            ""
        )
        fragmentHomeBinding.tvSavedAddress.text = CloudInPreferenceManager.getString(
            USER_LOCATION_STRING,
            ""
        )

        homeViewModel.isDashboardReceived.observe(dashboardActivity) {
            if (it) {
                fragmentHomeBinding.slDashboard.stopShimmer()
                fragmentHomeBinding.slDashboard.visibility = View.GONE

                dashboardBannerSliderListAdapter.deleteItem()
                dashboardBannerSliderListAdapter.addItem(homeViewModel.bannersList.value!!)

                if (homeViewModel.bannersList.value!!.size > 0) {
                    dashboardBannerSliderListAdapter.deleteItem()
                    dashboardBannerSliderListAdapter.addItem(
                        homeViewModel.bannersList.value!!
                    )
                    fragmentHomeBinding.bannerSlider.visibility = View.VISIBLE
                } else
                    fragmentHomeBinding.bannerSlider.visibility = View.GONE
            }
        }

        fragmentHomeBinding.llAddressHeader.setOnClickListener {
            val intent = CloudinPlacePicker.Builder(dashboardActivity)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE)
                .setMapType(MapType.NORMAL)
                .withLocation(
                    CloudInPreferenceManager.getString(USER_LAT, "")!!.toDouble(),
                    CloudInPreferenceManager.getString(USER_LONG, "")!!.toDouble()
                )
                .setCountry("IN")
                .setPickerLanguage(PickerLanguage.ENGLISH)
                .enableShowMapAfterSearchResult(true)
                .build()
            placePickerResultLauncher.launch(intent)
        }

        homeViewModel.bannersList.observe(dashboardActivity) {
            if (it.size > 0) {
                dashboardBannerSliderListAdapter.deleteItem()
                dashboardBannerSliderListAdapter.addItem(
                    homeViewModel.bannersList.value!!
                )
                fragmentHomeBinding.bannerSlider.visibility = View.VISIBLE
            } else
                fragmentHomeBinding.bannerSlider.visibility = View.GONE
        }

        fragmentHomeBinding.bannerSlider.setSliderAdapter(dashboardBannerSliderListAdapter)
        fragmentHomeBinding.bannerSlider.currentPagePosition = 0
        fragmentHomeBinding.bannerSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        fragmentHomeBinding.bannerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
    }

    override fun onResume() {
        super.onResume()
        fragmentHomeBinding.slDashboard.visibility = View.VISIBLE
        fragmentHomeBinding.slDashboard.startShimmer()
        if (CloudInPreferenceManager.getString(USER_UNIQUE_TOKEN, "").equals(""))
            homeViewModel.getUniqueToken()
        else
            homeViewModel.getDashboard()
    }

}
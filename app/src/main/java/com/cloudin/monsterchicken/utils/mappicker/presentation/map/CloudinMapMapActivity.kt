package com.cloudin.monsterchicken.utils.mappicker.presentation.map

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.databinding.ActivityVanillaMapBinding
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.USER_LAT
import com.cloudin.monsterchicken.utils.USER_LONG
import com.cloudin.monsterchicken.utils.mappicker.common.SafeObserver
import com.cloudin.monsterchicken.utils.mappicker.data.GeoCoderAddressResponse
import com.cloudin.monsterchicken.utils.mappicker.data.common.AddressMapperGoogleMap
import com.cloudin.monsterchicken.utils.mappicker.extenstion.hasExtra
import com.cloudin.monsterchicken.utils.mappicker.extenstion.hide
import com.cloudin.monsterchicken.utils.mappicker.extenstion.isRequiredField
import com.cloudin.monsterchicken.utils.mappicker.extenstion.openAppSetting
import com.cloudin.monsterchicken.utils.mappicker.extenstion.show
import com.cloudin.monsterchicken.utils.mappicker.extenstion.showAlertDialog
import com.cloudin.monsterchicken.utils.mappicker.presentation.builder.CloudinMapConfig
import com.cloudin.monsterchicken.utils.mappicker.presentation.builder.CloudinPlacePicker
import com.cloudin.monsterchicken.utils.mappicker.presentation.common.CloudinMapBaseViewModelActivity
import com.cloudin.monsterchicken.utils.mappicker.service.FetchAddressIntentService
import com.cloudin.monsterchicken.utils.mappicker.utils.AutoCompleteUtils
import com.cloudin.monsterchicken.utils.mappicker.utils.KeyUtils
import com.cloudin.monsterchicken.utils.mappicker.utils.Logger
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerType
import com.cloudin.monsterchicken.utils.mappicker.utils.SharedPrefs
import com.cloudin.monsterchicken.utils.mappicker.utils.ToastUtils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import org.json.JSONObject

class CloudinMapMapActivity :
    CloudinMapBaseViewModelActivity<ActivityVanillaMapBinding, CloudinMapViewModel>(),
    OnMapReadyCallback,
    View.OnClickListener {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private lateinit var resultReceiver: AddressResultReceiver
    private var selectedPlace: GeoCoderAddressResponse? = null
    private var midLatLng: LatLng? = null
    private var defaultZoomLoaded = false
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val startLocationHandler = Handler(Looper.getMainLooper())
    private var locationCallback: LocationCallback? = null

    private var cloudinMapConfig: CloudinMapConfig? = null

    // Belows are used in PlacePickerActivity
    private var isRequestedWithLocation = false
    private val sharedPrefs by lazy { SharedPrefs(this) }
    private var fetchLocationForFirstTime = false

    private var placePickerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    // data contains Place object
                    if (cloudinMapConfig?.enableShowMapAfterSearchResult == true) {
                        navigateMapToResult(result.data)
                    } else {
                        setResult(Activity.RESULT_OK, result.data)
                        finish()
                    }
                }
            }
        }

    override fun inflateLayout(layoutInflater: LayoutInflater) =
        ActivityVanillaMapBinding.inflate(layoutInflater)

    override fun buildViewModel(): CloudinMapViewModel {
        return ViewModelProvider(
            this,
            CloudinMapViewModelFactory(sharedPrefs, application)
        )[CloudinMapViewModel::class.java]
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_CANCELED -> viewModel.fetchSavedLocation()
                Activity.RESULT_OK -> postDelayed()
            }
        }

    override fun initViews() {
        super.initViews()
        // HIDE ActionBar(if exist in style) of root project module
        supportActionBar?.hide()
        setMapPinDrawable()
        with(binding.customToolbar) {
            tvAddress.isSelected = true
            ivBack.setOnClickListener(this@CloudinMapMapActivity)
            ivDone.setOnClickListener(this@CloudinMapMapActivity)
            if (cloudinMapConfig?.pickerType == PickerType.MAP_WITH_AUTO_COMPLETE) {
                tvAddress.setOnClickListener(this@CloudinMapMapActivity)
            }
        }
        binding.fabLocation.setOnClickListener(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        viewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        viewModel.appLogoutLiveData.observe(this) {
            logout(this@CloudinMapMapActivity)
        }

        viewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }

        if (!isRequestedWithLocation) {
            startLocationUpdates()
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        resultReceiver = AddressResultReceiver(Handler(Looper.getMainLooper()))

        viewModel.isServiceAvailable.observe(this@CloudinMapMapActivity) {
            if (it) {
                if (!viewModel.isServiceAvailableMessage.value.equals(""))
                    with(binding.customToolbar) {
                        ivDone.show()
                    }
            } else {
                with(binding.customToolbar) {
                    ivDone.hide()
                }
                if (!viewModel.isServiceAvailableMessage.value.equals(""))
                    showServiceNotAvailableDialog()
            }
        }
    }

    private fun showServiceNotAvailableDialog() {
        val customDialog = Dialog(this@CloudinMapMapActivity)
        customDialog.setContentView(R.layout.dialog_service_not_available)
        customDialog.setCancelable(false)
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val ivClose = customDialog.findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    override fun getBundle() {
        if (hasExtra(KeyUtils.EXTRA_CONFIG)) {
            cloudinMapConfig = intent.getParcelableExtra(KeyUtils.EXTRA_CONFIG)
        }
        if (cloudinMapConfig?.latitude != KeyUtils.DEFAULT_LOCATION && cloudinMapConfig?.longitude != KeyUtils.DEFAULT_LOCATION) {
            isRequestedWithLocation = true
            viewModel.latLngLiveData.postValue(
                LatLng(
                    cloudinMapConfig?.latitude!!,
                    cloudinMapConfig?.longitude!!
                )
            )
        }
        startLocationRequestPermission()
    }

    // Set custom image drawable to Map Pin
    private fun setMapPinDrawable() {
        try {
            cloudinMapConfig?.mapPinDrawable?.let { pinDrawableResId ->
                binding.ivMarker.setImageDrawable(ContextCompat.getDrawable(this, pinDrawableResId))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Invalid drawable resource ID. Error: $e")
        }
    }

    /**
     * this runnable will help to provide adequate delay before fetching device location.
     */
    private val startLocationRunnable = Runnable {
        getLocationFromFusedLocation()
    }

    private fun removeCallbacks() {
        if (locationCallback != null)
            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
        startLocationHandler.removeCallbacks(startLocationRunnable)
    }

    private fun postDelayed() {
        startLocationHandler.postDelayed(startLocationRunnable, KeyUtils.LOCATION_UPDATE_INTERVAL)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> onBackPressed()
            R.id.ivDone -> {
                selectedPlace ?: return
                stopLocationUpdates()
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(
                        KeyUtils.SELECTED_PLACE,
                        selectedPlace?.let { AddressMapperGoogleMap.apply(it) })
                })
                finish()
            }
            R.id.tvAddress ->
                placePickerResultLauncher.launch(cloudinMapConfig?.let {
                    AutoCompleteUtils.getAutoCompleteIntent(
                        this,
                        it
                    )
                })
            R.id.fabLocation -> isGpsEnabled()
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private inner class AddressResultReceiver(
        handler: Handler,
    ) : ResultReceiver(handler) {
        /**
         * Receives data sent from FetchAddressIntentService and updates the UI.
         */
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (!resultData.containsKey(KeyUtils.RESULT_DATA_KEY)) {
                return
            }
            when (resultCode) {
                KeyUtils.SUCCESS_RESULT -> {
                    selectedPlace =
                        resultData.getSerializable(KeyUtils.RESULT_DATA_KEY) as GeoCoderAddressResponse
                    with(binding.customToolbar) {
                        if (selectedPlace?.addressLine.isRequiredField()) {
//                            ivDone.show()
                            tvAddress.text = selectedPlace?.addressLine
                            val addressCheckJsonObject = JSONObject()
                            CloudInPreferenceManager.setString(
                                USER_LAT,
                                midLatLng!!.latitude.toString()
                            )
                            CloudInPreferenceManager.setString(
                                USER_LONG,
                                midLatLng!!.longitude.toString()
                            )
                            addressCheckJsonObject.put(
                                "latitude",
                                CloudInPreferenceManager.getString(USER_LAT, "")
                            )
                            addressCheckJsonObject.put(
                                "longitude",
                                CloudInPreferenceManager.getString(USER_LONG, "")
                            )
                            addressCheckJsonObject.put("cityId", selectedPlace?.locality)
                            addressCheckJsonObject.put("pinCode", selectedPlace?.postalCode)
                            if (viewModel.latLngLiveData.value!!.latitude > 0)
                                viewModel.checkAddressAvailable(addressCheckJsonObject)
//                            NativeUtils.ErrorLog("featureName", selectedPlace?.addressLine)
//                            NativeUtils.ErrorLog("adminArea", selectedPlace?.adminArea)
//                            NativeUtils.ErrorLog("subAdminArea", selectedPlace?.subAdminArea)
//                            NativeUtils.ErrorLog("locality", selectedPlace?.locality)
//                            NativeUtils.ErrorLog("thoroughfare", selectedPlace?.thoroughfare)
//                            NativeUtils.ErrorLog("postalCode", selectedPlace?.postalCode)
//                            NativeUtils.ErrorLog("countryCode", selectedPlace?.countryCode)
//                            NativeUtils.ErrorLog("phone", selectedPlace?.phone)
                        } else {
                            ivDone.hide()
                        }
                    }
                }
                KeyUtils.FAILURE_RESULT -> {
                    val errorMessage = resultData.getString(KeyUtils.RESULT_MESSAGE_KEY)
                    ToastUtils.showToast(this@CloudinMapMapActivity, errorMessage)
                }
                else -> {
                    // make address empty
                }
            }
        }
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready for use.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap?.clear()
        if (cloudinMapConfig?.enableSatelliteView == true)
            this.googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE

        // Customise the styling of the base map using a JSON object defined...
        try {
            // ...in a raw resource file.
            if (cloudinMapConfig?.mapStyleJSONResId != KeyUtils.DEFAULT_STYLE_JSON_RESID) {
                this.googleMap?.setMapStyle(
                    cloudinMapConfig?.let {
                        MapStyleOptions.loadRawResourceStyle(
                            this@CloudinMapMapActivity,
                            it.mapStyleJSONResId
                        )
                    }
                )
            }
            // ...in a string resource file.
            this.googleMap?.mapType = cloudinMapConfig.let { it?.mapType?.value!! }
        } catch (e: Exception) {
            Logger.e(TAG, "Can't find map style or Style parsing failed. Error: $e")
        }
        val cameraUpdateDefaultLocation =
            cloudinMapConfig?.let { cloudinMapConfig?.latitude?.let { it1 -> LatLng(it1, it.longitude) } }
                ?.let {
                    CameraUpdateFactory.newLatLngZoom(
                        it,
                        if (cloudinMapConfig?.latitude == KeyUtils.DEFAULT_LOCATION) 0f else KeyUtils.DEFAULT_ZOOM_LEVEL
                    )
                }
        if (cameraUpdateDefaultLocation != null) {
            this.googleMap?.animateCamera(
                cameraUpdateDefaultLocation,
                KeyUtils.GOOGLE_MAP_CAMERA_ANIMATE_DURATION,
                null
            )
        }
        /**
         * Set Padding: Top to show CompassButton at visible position on map
         * (Before allow Location runtime permission, After that changed position of CompassButton)
         * */
        this.googleMap?.setPadding(0, 256, 0, 0)
        cloudinMapConfig?.zoneRect?.let {
            this.googleMap?.setLatLngBoundsForCameraTarget(
                LatLngBounds(
                    it.lowerLeft,
                    it.upperRight
                )
            )
        }

        googleMap.setOnCameraMoveListener {
            with(binding.customToolbar)
            {
                tvAddress.text = getString(R.string.searching)
                ivDone.hide()
            }
        }
        googleMap.setOnCameraIdleListener {
            val newLatLng = this@CloudinMapMapActivity.googleMap?.cameraPosition?.target
            newLatLng?.let {
                midLatLng?.let { midLatLng ->
                    if (it.latitude == midLatLng.latitude && it.longitude == midLatLng.longitude) {
                        return@setOnCameraIdleListener
                    }
                }
                midLatLng = this@CloudinMapMapActivity.googleMap?.cameraPosition?.target
                midLatLng?.let { centerPoint ->
                    startReverseGeoCodingService(centerPoint)
                }
            }
        }
    }

    override fun initLiveDataObservers() {
        super.initLiveDataObservers()
        viewModel.latLngLiveData.observe(this, SafeObserver(this::moveCameraToLocation))
    }

    private fun moveCameraToLocation(latLng: LatLng?) {
        latLng ?: return

        val zoomLevel = if (defaultZoomLoaded) {
            defaultZoomLoaded = false
            googleMap?.cameraPosition?.zoom ?: KeyUtils.DEFAULT_ZOOM_LEVEL
        } else {
            KeyUtils.DEFAULT_ZOOM_LEVEL
        }
        val location = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
        googleMap?.animateCamera(location)

        changeLocationCompassButtonPosition()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            this.googleMap?.isMyLocationEnabled = true
            this.googleMap?.uiSettings?.isMyLocationButtonEnabled = false
            changeMyLocationButtonPosition()
        }
    }

    /*
    * Change location compass button position
    * From: Default
    * To: Top - Right
    * */
    private fun changeLocationCompassButtonPosition() {
        try {
            val locationCompassButton =
                (mapFragment?.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View)
                    .findViewById<View>(Integer.parseInt("5"))
            val rlp = locationCompassButton.layoutParams as RelativeLayout.LayoutParams
            rlp.addRule(RelativeLayout.ALIGN_PARENT_START, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
            rlp.marginEnd = 16
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /*
    * Change my location button position
    * From: Default
    * To: Bottom - Right
    * */
    private fun changeMyLocationButtonPosition() {
        val locationButton =
            (mapFragment?.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View)
                .findViewById<View>(Integer.parseInt("2"))
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 0, 32)
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private fun startReverseGeoCodingService(latLng: LatLng) {
        val lat = latLng.latitude
        val lng = latLng.longitude

        if (lat != KeyUtils.DEFAULT_LOCATION && lng != KeyUtils.DEFAULT_LOCATION) {
            // Create an intent for passing to the intent service responsible for fetching the address.
            val intent = Intent(this, FetchAddressIntentService::class.java).apply {
                // Pass the result receiver as an extra to the service.
                putExtra(KeyUtils.RECEIVER, resultReceiver)
                // Pass the location data as an extra to the service.
                putExtra(KeyUtils.LOCATION_DATA_EXTRA, latLng)
            }
            // Start the service. If the service isn't already running, it is instantiated and started
            // (creating a process for it if needed); if it is running then it remains running. The
            // service kills itself automatically once all intents are processed.
            startService(intent)
        }
    }

    private fun navigateMapToResult(data: Intent?) {
        val vanillaAddress = CloudinPlacePicker.getPlaceResult(data)
        this.googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    vanillaAddress?.latitude ?: KeyUtils.DEFAULT_LOCATION,
                    vanillaAddress?.longitude ?: KeyUtils.DEFAULT_LOCATION
                ),
                KeyUtils.DEFAULT_ZOOM_LEVEL
            ),
            KeyUtils.GOOGLE_MAP_CAMERA_ANIMATE_DURATION,
            null
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            KeyUtils.REQUEST_PERMISSIONS_REQUEST_CODE -> {
                when {
                    grantResults.isEmpty() -> {
                        // If user interaction was interrupted, the permission request is cancelled and you receive empty arrays.
                        Log.d(TAG, resources.getString(R.string.user_interaction_was_cancelled))
                    }
                    grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        if (!isRequestedWithLocation) {
                            startLocationUpdates()
                        }

                    }
                    else -> showAlertDialog(
                        R.string.missing_permission_message,
                        R.string.missing_permission_title,
                        R.string.permission,
                        R.string.cancel, {
                            // this mean user has clicked on permission button to update run time permission.
                            openAppSetting()
                        }, {
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun startLocationRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // this mean device os is greater or equal to Marshmallow.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // here we are going to request location run time permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    KeyUtils.REQUEST_PERMISSIONS_REQUEST_CODE
                )
                return
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        this.priority = Priority.PRIORITY_HIGH_ACCURACY
        this.interval = KeyUtils.DEFAULT_FETCH_LOCATION_INTERVAL
    }

    private val locationSettingRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)

    /**
     * this method will check required for location and according to result it will go ahead for fetching location.
     */
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        LocationServices.getSettingsClient(this)
            .checkLocationSettings(locationSettingRequest.build())
            .addOnSuccessListener(this) {
                getLocationFromFusedLocation()
            }.addOnFailureListener(this) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Logger.i(
                            TAG,
                            resources.getString(R.string.location_settings_are_not_satisfied)
                        )
                        try {
                            val rae = e as ResolvableApiException
                            launcher.launch(IntentSenderRequest.Builder(rae.resolution).build())
                        } catch (sie: IntentSender.SendIntentException) {
                            Logger.i(
                                TAG,
                                getString(R.string.pendingintent_unable_to_execute_request)
                            )
                            viewModel.fetchSavedLocation()
                            sie.printStackTrace()
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            resources.getString(R.string.location_settings_are_inadequate_and_cannot_be_fixed_here)
                        Logger.e(TAG, errorMessage)
                        viewModel.fetchSavedLocation()
                    }
                }
            }
    }

    private fun getLocationFromFusedLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                Log.e("location", location.toString())
                if (location != null) {
                    viewModel.saveLatLngToSharedPref(location.latitude, location.longitude)
                }
                if (!fetchLocationForFirstTime) {
                    viewModel.fetchSavedLocation()
                    fetchLocationForFirstTime = true
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                if (!locationAvailability.isLocationAvailable) {
                    viewModel.fetchSavedLocation()
                }
            }
        }
        fusedLocationProviderClient?.flushLocations()
        Looper.myLooper()?.let {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                it
            )
        }
    }


    private fun stopLocationUpdates() {
        // here, we are removing callback from runnable for handler so it will get called ahead.
        removeCallbacks()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun isGpsEnabled() {
        if (!fetchLocationForFirstTime) {
            startLocationUpdates()
        } else {
            viewModel.fetchSavedLocation()
        }
    }
}

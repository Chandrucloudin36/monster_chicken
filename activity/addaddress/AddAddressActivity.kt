package com.cloudin.monsterchicken.activity.addaddress

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.cloudin.greenbound.utils.commondialog.CloudInCommonListDialog
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.activity.addresslist.AddressesList
import com.cloudin.monsterchicken.baseApiCalls.errorDialog
import com.cloudin.monsterchicken.baseApiCalls.logout
import com.cloudin.monsterchicken.baseApiCalls.showLoader
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityAddAddressBinding
import com.cloudin.monsterchicken.utils.CloudInPreferenceManager
import com.cloudin.monsterchicken.utils.USER_PHONE_NUMBER
import com.cloudin.monsterchicken.utils.commondialog.CommonListResponse
import com.cloudin.monsterchicken.utils.mappicker.presentation.builder.CloudinPlacePicker
import com.cloudin.monsterchicken.utils.mappicker.utils.MapType
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerLanguage
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerType
import com.cloudin.monsterchicken.utils.mappicker.utils.ToastUtils
import org.json.JSONObject

class AddAddressActivity : CloudInBaseActivity() {

    private lateinit var addAddressBinding: ActivityAddAddressBinding
    private val addAddressViewModel: AddAddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addAddressBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_address)

        addAddressBinding.addAddressViewModel = addAddressViewModel
        addAddressBinding.lifecycleOwner = this
        setContentView(addAddressBinding.root)

        initView()
    }

    private var placePickerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val vanillaAddress = CloudinPlacePicker.getPlaceResult(result.data)
                vanillaAddress?.let {
                    addAddressViewModel.lattitude.value = it.latitude.toString()
                    addAddressViewModel.longitude.value = it.longitude.toString()
                    addAddressViewModel.areaAndLocality.value = it.formattedAddress
                    addAddressViewModel.state.value = it.stateCode
                    addAddressViewModel.city.value = it.locality
                    addAddressViewModel.pinCode.value = it.postalCode
                }
            }
        }

    private fun initView() {
        addAddressViewModel.errorListLiveData.observe(this) {
            errorDialog(it, this)
        }

        addAddressViewModel.appLogoutLiveData.observe(this) {
            logout(this@AddAddressActivity)
        }

        addAddressViewModel.loaderFlagLiveData.observe(this) {
            this.showLoader(it)
        }
        addAddressBinding.ivBackIcon.setOnClickListener { onBackPressed() }

        if (intent.getSerializableExtra("ADDRESS_DETAILS") != null) {
            val addressDetails = intent.getSerializableExtra("ADDRESS_DETAILS") as AddressesList?
            if (addressDetails != null) {
                addAddressViewModel.areaAndLocality.value = addressDetails.area
                addAddressViewModel.lattitude.value = addressDetails.latitude
                addAddressViewModel.longitude.value = addressDetails.longitude
                addAddressViewModel.flatNumber.value = addressDetails.streetName
                addAddressViewModel.landmark.value = addressDetails.landMark
                addAddressViewModel.city.value = addressDetails.city
                addAddressViewModel.pinCode.value = addressDetails.pincode
                addAddressViewModel.mobileNumber.value = addressDetails.number
                addAddressViewModel.saveAs.value = addressDetails.type
                addAddressViewModel.name.value = addressDetails.nickName
                addAddressViewModel.stateNameText.value = addressDetails.state.state
                addAddressViewModel.stateIdText.value = addressDetails.state.id.toString()
                addAddressViewModel.districtNameText.value = addressDetails.district.state
                addAddressViewModel.districtIdText.value = addressDetails.district.id.toString()

                when (addressDetails.type) {
                    "home" -> setHomeSelected()
                    "work" -> setWorkSelected()
                    else -> {
                        addAddressViewModel.nickname.value = addressDetails.nickName
                        setOtherSelected()
                    }
                }

                addAddressViewModel.addressId.value = addressDetails.id
            }
        }

        if (addAddressViewModel.addressId.value.equals("")) {
            val intent = CloudinPlacePicker.Builder(this)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE)
                .setMapType(MapType.NORMAL)
//            .withLocation(23.0710, 72.5181)
                .setCountry("IN")
                .setPickerLanguage(PickerLanguage.ENGLISH)
                .enableShowMapAfterSearchResult(true)
                .build()
            placePickerResultLauncher.launch(intent)
            setHomeSelected()
            if (!CloudInPreferenceManager.getString(USER_PHONE_NUMBER, "").equals(""))
                addAddressViewModel.mobileNumber.value = CloudInPreferenceManager.getString(
                    USER_PHONE_NUMBER,
                    ""
                )
        } else {
            val intent = CloudinPlacePicker.Builder(this)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE)
                .setMapType(MapType.NORMAL)
                .withLocation(
                    addAddressViewModel.lattitude.value!!.toDouble(),
                    addAddressViewModel.longitude.value!!.toDouble()
                )
                .setCountry("IN")
                .setPickerLanguage(PickerLanguage.ENGLISH)
                .enableShowMapAfterSearchResult(true)
                .build()
            placePickerResultLauncher.launch(intent)
        }

        addAddressBinding.llHome.setOnClickListener {
            setHomeSelected()
        }
        addAddressBinding.llWork.setOnClickListener {
            setWorkSelected()
        }
        addAddressBinding.llOther.setOnClickListener {
            setOtherSelected()
        }

        addAddressBinding.tvSaveButton.setOnClickListener { checkAllFields() }

        addAddressBinding.tetState.setOnClickListener {
            addAddressViewModel.callGetStatesList()
        }

        addAddressBinding.tetDistrict.setOnClickListener {
            if (!addAddressViewModel.stateIdText.value.equals(""))
                addAddressViewModel.callGetDistrictList()
            else
                ToastUtils.showToast(
                    this@AddAddressActivity,
                    getString(R.string.add_address_please_select_state)
                )
        }

        addAddressViewModel.stateResponse.observe(this@AddAddressActivity) {
            if (it)
                if (addAddressViewModel.statesList.isNotEmpty())
                    showStatesListDialog()
                else
                    ToastUtils.showToast(this@AddAddressActivity, "State list is empty")
        }

        addAddressViewModel.districtResponse.observe(this@AddAddressActivity) {
            if (it)
                if (addAddressViewModel.districtList.isNotEmpty())
                    showDistrictListDialog()
                else
                    ToastUtils.showToast(this@AddAddressActivity, "District list is empty")
        }

        addAddressViewModel.addressAdded.observe(this) {
            if (it) {
                addAddressViewModel.addressAdded.value = false
                finish()
            }
        }

    }

    private fun showStatesListDialog() {
        CloudInCommonListDialog.showCloudInCommonListDialog(this,
            getString(R.string.add_address_activity_please_select_state),
            addAddressViewModel.statesList,
            listener = object : CloudInCommonListDialog.OnItemSelect {
                override fun onSelectItemClick(dialogModel: CommonListResponse.CommonList) {
                    addAddressViewModel.stateNameText.value = dialogModel.name
                    addAddressViewModel.stateIdText.value = dialogModel.id
                    addAddressViewModel.districtNameText.value = ""
                    addAddressViewModel.districtIdText.value = ""
                }
            })
    }

    private fun showDistrictListDialog() {
        CloudInCommonListDialog.showCloudInCommonListDialog(this,
            getString(R.string.add_address_activity_please_select_district),
            addAddressViewModel.districtList,
            listener = object : CloudInCommonListDialog.OnItemSelect {
                override fun onSelectItemClick(dialogModel: CommonListResponse.CommonList) {
                    addAddressViewModel.districtNameText.value = dialogModel.name
                    addAddressViewModel.districtIdText.value = dialogModel.id
                }
            })
    }

    private fun checkAllFields() {
        val mobileNumberString = addAddressViewModel.mobileNumber.value
        val flatNumberString = addAddressViewModel.flatNumber.value
        val saveAs = addAddressViewModel.saveAs.value
        val name = addAddressViewModel.name.value
        val landmarkString = addAddressViewModel.landmark.value
        val nickNameString = addAddressViewModel.nickname.value
        val stateId = addAddressViewModel.stateIdText.value
        val districtId = addAddressViewModel.districtIdText.value
        if (flatNumberString == null || flatNumberString == "" || flatNumberString.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.add_address_activity_enter_flat_no))
            return
        } else if (name == null || name == "" || name.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.add_address_activity_please_enter_name))
            return
        } else if (landmarkString == null || landmarkString == "" || landmarkString.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.add_address_activity_enter_landmark))
            return
        } else if (mobileNumberString == null || mobileNumberString == "" || mobileNumberString.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.add_address_activity_enter_mobile_number))
            return
        } else if (mobileNumberString.length <= 9) {
            ToastUtils.showToast(
                this,
                getString(R.string.add_address_activity_enter_valid_mobile_number)
            )
            return
        } else if (saveAs.equals("others") && (nickNameString == null || nickNameString == "" || nickNameString.isEmpty())) {
            ToastUtils.showToast(this, getString(R.string.add_address_activity_enter_nick_name))
            return
        } else if (stateId == null || stateId == "" || stateId.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.add_address_activity_please_select_state))
            return
        } else if (districtId == null || districtId == "" || districtId.isEmpty()) {
            ToastUtils.showToast(
                this,
                getString(R.string.add_address_activity_please_select_district)
            )
            return
        } else {
            val requestToAddAddressJSONObject = JSONObject()
            requestToAddAddressJSONObject.put(
                "area",
                addAddressViewModel.areaAndLocality.value
            )
            requestToAddAddressJSONObject.put(
                "name",
                name
            )
            requestToAddAddressJSONObject.put(
                "street_name",
                addAddressViewModel.flatNumber.value
            )
            requestToAddAddressJSONObject.put(
                "latitude",
                addAddressViewModel.lattitude.value
            )
            requestToAddAddressJSONObject.put(
                "longitude",
                addAddressViewModel.longitude.value
            )
            requestToAddAddressJSONObject.put(
                "landmark",
                addAddressViewModel.landmark.value
            )
            requestToAddAddressJSONObject.put(
                "city",
                addAddressViewModel.city.value
            )
            requestToAddAddressJSONObject.put(
                "number",
                addAddressViewModel.mobileNumber.value
            )
            requestToAddAddressJSONObject.put(
                "pincode",
                addAddressViewModel.pinCode.value
            )
            requestToAddAddressJSONObject.put(
                "type",
                addAddressViewModel.saveAs.value
            )
            if (addAddressViewModel.saveAs.value.equals("others"))
                requestToAddAddressJSONObject.put(
                    "nick_name",
                    addAddressViewModel.nickname.value
                )
            if (!addAddressViewModel.addressId.value.equals(""))
                requestToAddAddressJSONObject.put(
                    "address_id",
                    addAddressViewModel.addressId.value
                )
            requestToAddAddressJSONObject.put(
                "state_id",
                addAddressViewModel.stateIdText.value
            )
            requestToAddAddressJSONObject.put(
                "district_id",
                addAddressViewModel.districtIdText.value
            )

            addAddressViewModel.addAddress(requestToAddAddressJSONObject)
        }
    }

    private fun setHomeSelected() {
        addAddressViewModel.saveAs.value = "home"
        addAddressBinding.llHome.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock_selected, theme)
        addAddressBinding.llWork.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        addAddressBinding.llOther.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        addAddressBinding.tetNickName.visibility = View.GONE
    }

    private fun setWorkSelected() {
        addAddressViewModel.saveAs.value = "work"
        addAddressBinding.llWork.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock_selected, theme)
        addAddressBinding.llHome.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        addAddressBinding.llOther.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        addAddressBinding.tetNickName.visibility = View.GONE
    }

    private fun setOtherSelected() {
        addAddressViewModel.saveAs.value = "others"
        addAddressBinding.llOther.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock_selected, theme)
        addAddressBinding.llWork.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        addAddressBinding.llHome.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_white_with_stock, theme)
        addAddressBinding.tetNickName.visibility = View.VISIBLE
    }
}
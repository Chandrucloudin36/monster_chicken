package com.cloudin.monsterchicken.utils.mappicker.presentation.builder

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.cloudin.monsterchicken.utils.mappicker.utils.KeyUtils
import com.cloudin.monsterchicken.utils.mappicker.utils.MapType
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerType
import kotlinx.parcelize.Parcelize

@Parcelize
data class CloudinMapConfig(
    var apiKey: String? = "",
    var pickerType: PickerType = PickerType.MAP_WITH_AUTO_COMPLETE,
    var country: String? = null,
    var latitude: Double = KeyUtils.DEFAULT_LOCATION,
    var longitude: Double = KeyUtils.DEFAULT_LOCATION,
    var radius: Int? = null,
    var language: String? = null,
    var types: String? = null,
    var zoneRect: SearchZoneRect? = null,
    var enableSatelliteView: Boolean = false,
    var mapStyleJSONResId: Int = KeyUtils.DEFAULT_STYLE_JSON_RESID,
    var mapType: MapType = MapType.NORMAL,
    var enableShowMapAfterSearchResult: Boolean = false,
    @DrawableRes var mapPinDrawable: Int? = null
) : Parcelable
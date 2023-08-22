package com.cloudin.monsterchicken.utils.mappicker.presentation.builder
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.cloudin.monsterchicken.utils.mappicker.data.CloudinMapAddress
import com.cloudin.monsterchicken.utils.mappicker.data.common.AutoCompleteAddressDetailsMapper
import com.cloudin.monsterchicken.utils.mappicker.extenstion.isRequiredField
import com.cloudin.monsterchicken.utils.mappicker.presentation.map.CloudinMapMapActivity
import com.cloudin.monsterchicken.utils.mappicker.utils.AutoCompleteUtils
import com.cloudin.monsterchicken.utils.mappicker.utils.BundleUtils
import com.cloudin.monsterchicken.utils.mappicker.utils.KeyUtils
import com.cloudin.monsterchicken.utils.mappicker.utils.MapType
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerLanguage
import com.cloudin.monsterchicken.utils.mappicker.utils.PickerType
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.Autocomplete
import wrap


class CloudinPlacePicker {

    companion object {
        private val TAG = CloudinPlacePicker::class.java.simpleName

        fun getPlaceResult(data: Intent?): CloudinMapAddress? {
            if (data != null) {
                val selectedPlace = data.getSerializableExtra(KeyUtils.SELECTED_PLACE)
                return if (selectedPlace is CloudinMapAddress) {
                    selectedPlace
                } else {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    AutoCompleteAddressDetailsMapper.apply(place)
                }
            }
            return null
        }
    }

    class Builder(private val context: Context) {
        private val cloudinMapConfig = CloudinMapConfig()

        /**
         * To enable map view with place picker
         */
        fun with(pickerType: PickerType): Builder {
            cloudinMapConfig.pickerType = pickerType
            return this
        }

        /**
         * Filter addresses by country name ex. "US"
         */
        fun setCountry(country: String): Builder {
            cloudinMapConfig.country = country
            return this
        }

        /**
         * Request with default latitude & longitude for near by places
         */
        fun withLocation(latitude: Double, longitude: Double): Builder {
            cloudinMapConfig.latitude = latitude
            cloudinMapConfig.longitude = longitude
            return this
        }

        /**
         * To restrict request zone by rect
         */
        fun setLocationRestriction(leftLatLng: LatLng, rightLatLng: LatLng): Builder {
            cloudinMapConfig.zoneRect = SearchZoneRect(leftLatLng, rightLatLng)
            return this
        }

        /**
         * Add a map with custom styling
         * With style options you can customize the presentation of the standard Google map styles,
         * changing the visual display of features like roads, parks, businesses, and other points of interest.
         * Add a resource containing a JSON style object (Use a raw resource)
         * */
        fun setMapStyle(jsonResourceIdMapStyle: Int): Builder {
            cloudinMapConfig.mapStyleJSONResId = jsonResourceIdMapStyle
            return this
        }

        /**
         * Add a map with custom styling
         * With style options you can customize the presentation of the standard Google map styles,
         * changing the visual display of features like roads, parks, businesses, and other points of interest.
         * Add a resource containing a JSON style object (Use a string resource)
         * */
        fun setMapType(mapType: MapType): Builder {
            cloudinMapConfig.mapType = mapType
            return this
        }

        /**
         * To show the map after selecting the place from place picker
         * To insure that user needs to navigate to map screen after the selecting the place from the place picker
         * @param  enableShowMapAfterSearchResult true to show the map after search result, false otherwise
         * @return Returns a VanillaPlacePicker.Builder instance.
         */
        fun enableShowMapAfterSearchResult(enableShowMapAfterSearchResult: Boolean): Builder {
            cloudinMapConfig.enableShowMapAfterSearchResult = enableShowMapAfterSearchResult
            return this
        }

        /**
         * Set custom Map Pin image
         * */
        fun setMapPinDrawable(mapPinDrawableResId: Int): Builder {
            cloudinMapConfig.mapPinDrawable = mapPinDrawableResId
            return this
        }

        /**
         * Set picker language
         */
        fun setPickerLanguage(pickerLanguage: PickerLanguage): Builder {
            ContextWrapper(context).wrap(pickerLanguage.value)
            return this
        }

        /**
         * Get Google Places API key
         */
        private fun getApiKey(): String? {
            val metadataBundle: Bundle = BundleUtils.getMetaData(context)
            return if (metadataBundle.getString("com.google.android.geo.API_KEY").isRequiredField())
                metadataBundle.getString("com.google.android.geo.API_KEY")
            else {
                Log.e(
                    TAG,
                    "Couldn't get Google API key from application meta data. Was it set in your AndroidManifest.xml?"
                )
                ""
            }
        }

        fun build(): Intent {
            cloudinMapConfig.apiKey = getApiKey()
            val intent = if (cloudinMapConfig.pickerType == PickerType.AUTO_COMPLETE) {
                AutoCompleteUtils.getAutoCompleteIntent(context, cloudinMapConfig)
            } else {
                Intent(context, CloudinMapMapActivity::class.java)
            }
            intent.putExtra(KeyUtils.EXTRA_CONFIG, cloudinMapConfig)
            return intent
        }
    }
}
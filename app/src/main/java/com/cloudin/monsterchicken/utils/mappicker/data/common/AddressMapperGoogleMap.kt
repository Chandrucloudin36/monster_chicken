package com.cloudin.monsterchicken.utils.mappicker.data.common

import com.cloudin.monsterchicken.utils.mappicker.data.CloudinMapAddress
import com.cloudin.monsterchicken.utils.mappicker.data.GeoCoderAddressResponse

object AddressMapperGoogleMap : BaseMapper<GeoCoderAddressResponse, CloudinMapAddress>() {

    override fun map(oldItem: GeoCoderAddressResponse): CloudinMapAddress {
        return CloudinMapAddress().apply {
            this.formattedAddress = oldItem.addressLine
            this.name = oldItem.featureName
            this.locality = oldItem.locality
            this.subLocality = oldItem.subAdminArea
            this.latitude = oldItem.latitude
            this.longitude = oldItem.longitude
            this.postalCode = oldItem.postalCode
            this.stateCode = getStateCodeFromFullName(oldItem.adminArea!!)
            this.countryCode = oldItem.countryCode
            this.countryName = oldItem.countryName
        }
    }

    private fun getStateCodeFromFullName(stateFullName: String): String {
        val stateCodeMap = mapOf(
            "Andhra Pradesh" to "AP",
            "Arunachal Pradesh" to "AR",
            "Assam" to "AS",
            "Bihar" to "BR",
            "Chhattisgarh" to "CG",
            "Goa" to "GA",
            "Gujarat" to "GJ",
            "Haryana" to "HR",
            "Himachal Pradesh" to "HP",
            "Jharkhand" to "JH",
            "Karnataka" to "KA",
            "Kerala" to "KL",
            "Madhya Pradesh" to "MP",
            "Maharashtra" to "MH",
            "Manipur" to "MN",
            "Meghalaya" to "ML",
            "Mizoram" to "MZ",
            "Nagaland" to "NL",
            "Odisha" to "OD",
            "Punjab" to "PB",
            "Rajasthan" to "RJ",
            "Sikkim" to "SK",
            "Tamil Nadu" to "TN",
            "Telangana" to "TG",
            "Tripura" to "TR",
            "Uttar Pradesh" to "UP",
            "Uttarakhand" to "UK",
            "West Bengal" to "WB",
            "Andaman and Nicobar Islands" to "AN",
            "Chandigarh" to "CH",
            "Dadra and Nagar Haveli and Daman and Diu" to "DN",
            "Delhi" to "DL",
            "Ladakh" to "LA",
            "Lakshadweep" to "LD",
            "Puducherry" to "PY"
        )

        return stateCodeMap[stateFullName] ?: ""
    }
}
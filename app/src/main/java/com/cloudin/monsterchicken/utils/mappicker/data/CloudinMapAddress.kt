package com.cloudin.monsterchicken.utils.mappicker.data

import java.io.Serializable

data class CloudinMapAddress(
        var formattedAddress: String? = null,
        var name: String? = null,
        var placeId: String? = null,
        var latitude: Double? = null,
        var longitude: Double? = null,
        var locality: String? = null,
        var subLocality: String? = null,
        var postalCode: String? = null,
        var stateCode: String? = null,
        var countryCode: String? = null,
        var countryName: String? = null
) : Serializable
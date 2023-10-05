package com.mehedisoftdev.barikoimapapps.repository

import android.content.Context
import com.mehedisoftdev.barikoimapapps.R
import com.mehedisoftdev.barikoimapapps.api.BarikoiApi
import com.mehedisoftdev.barikoimapapps.models.Place
import javax.inject.Inject

class NearbyBankLocationRepo @Inject constructor(
    private val barikoiApi: BarikoiApi
) {
    suspend fun getNearbyPlaces(
        context: Context,
        category: String,
        distance: Double,
        limit: Int,
        lon: Double,
        lat: Double
    ): List<Place> {
        val key = context.getString(R.string.barikoi_api_key)

        val result = barikoiApi.getNearbyPlaces(
            key,
            distance,
            limit,
            lon,
            lat,
            category
        )
        return result.body()!!.places
    }


}
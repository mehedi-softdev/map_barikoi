package com.mehedisoftdev.barikoimapapps.api

import com.mehedisoftdev.barikoimapapps.models.Places
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BarikoiApi {
    @GET("/v2/api/search/nearby/category/{key}/{distance}/{limit}")
    suspend fun getNearbyPlaces(
        @Path("key") key: String,
        @Path("distance") distance: Double,
        @Path("limit") limit: Int,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("ptype") category: String,
    ): Response<Places>
}
package com.mehedisoftdev.barikoimapapps.models

data class Place(
    val Address: String,
    val area: String,
    val city: String,
    val distance_in_meters: String,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val pType: String,
    val postCode: String,
    val subType: String,
    val uCode: String
)
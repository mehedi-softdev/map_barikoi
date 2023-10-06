package com.mehedisoftdev.barikoimapapps.adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mehedisoftdev.barikoimapapps.R
import com.mehedisoftdev.barikoimapapps.models.Place

class CustomInfoWindowAdapter(private val mapView: MapView, private val bank: Place) : MapboxMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: com.mapbox.mapboxsdk.annotations.Marker): View? {
        val inflater = LayoutInflater.from(mapView.context)
        val view = inflater.inflate(R.layout.info_window, null)

        val bankName = view.findViewById<TextView>(R.id.bank_name)
        bankName.text = bank.name ?: ""

        val subtype = view.findViewById<TextView>(R.id.sub_type)
        subtype.text = bank.subType ?: ""

        val coordinates = view.findViewById<TextView>(R.id.coordinates)
        coordinates.text = "(${bank.longitude}, ${bank.latitude})" ?: ""

        return view
    }

}
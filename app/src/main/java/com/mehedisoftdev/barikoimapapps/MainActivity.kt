package com.mehedisoftdev.barikoimapapps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.engine.LocationEngineRequest
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mehedisoftdev.barikoimapapps.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 50

    // users location tracking
    private lateinit var mapboxMap: MapboxMap
    private var lastLocation: Location? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // Init Maplibre
        Mapbox.getInstance(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // get users current location and focus on map
        getUsersLocation()

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getUsersLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                lastLocation = location
                focusLocation()
            }
    }


    private fun focusLocation() {
        val BARIKOI_API_KEY = getString(R.string.barikoi_api_key)
        val styleUri =
            "https://map.barikoi.com/styles/barikoi-bangla/style.json?key=$BARIKOI_API_KEY"

        binding.mapView.getMapAsync { map ->
            mapboxMap = map
            map.setStyle(
                Style.Builder().fromUri(styleUri)
            ) { style: Style ->
                // do later operation
                trackUserLocation(style)
            }
            mapboxMap.cameraPosition = CameraPosition
                .Builder()
                .target(
                    LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
                )
                .zoom(7.0)
                .build()
        }
    }

    private fun trackUserLocation(style: Style) {
        // tracking user's location on the map
        val locationComponent = mapboxMap.locationComponent
        val locationComponentOptions =
            LocationComponentOptions.builder(this@MainActivity)
                .pulseEnabled(true)
                .pulseColor(Color.GREEN)
                .foregroundTintColor(Color.RED)
                .build()
        val locationComponentActivationOptions =
            buildLocationComponentActivationOptions(style, locationComponentOptions)
        locationComponent.activateLocationComponent(locationComponentActivationOptions)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING

    }

    private fun buildLocationComponentActivationOptions(
        style: Style,
        locationComponentOptions: LocationComponentOptions
    ): LocationComponentActivationOptions {
        return LocationComponentActivationOptions
            .builder(this, style)
            .locationComponentOptions(locationComponentOptions)
            .useDefaultLocationEngine(true)
            .locationEngineRequest(
                LocationEngineRequest.Builder(750)
                    .setFastestInterval(750)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .build()
            )
            .build()
    }



    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        binding.mapView.onSaveInstanceState(outState)
    }


}
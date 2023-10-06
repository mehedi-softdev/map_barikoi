package com.mehedisoftdev.barikoimapapps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.engine.LocationEngineRequest
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mehedisoftdev.barikoimapapps.databinding.FragmentMapBinding
import com.mehedisoftdev.barikoimapapps.models.Place
import com.mehedisoftdev.barikoimapapps.utils.Constants
import com.mehedisoftdev.barikoimapapps.viewmodels.NearbyBankLocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding
        get() = _binding!!

    // variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 50

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted
                fetchUserLocation()
            } else {
                // do nothing for now
            }
        }

    // users location tracking
    private lateinit var mapboxMap: MapboxMap
    private var lastLocation: Location? = null
    private val nearbyBankLocationViewModel by viewModels<NearbyBankLocationViewModel>()


    // for info window
    private val bankMarkers = HashMap<Marker, Place>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // Init Maplibre
        Mapbox.getInstance(requireContext())



        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Check and request location permission when needed
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Permission is already granted, fetch user's location
            fetchUserLocation()
        }


    }


    @SuppressLint("MissingPermission")
    private fun fetchUserLocation() {
        fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                lastLocation = location
                focusUserLocation()
            }
    }


    private fun focusUserLocation() {
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
                .zoom(15.0)
                .build()
        }
    }

    private fun trackUserLocation(style: Style) {
        // tracking user's location on the map
        val locationComponent = mapboxMap.locationComponent
        val locationComponentOptions =
            LocationComponentOptions.builder(requireContext())
                .pulseEnabled(true)
                .pulseColor(Color.GREEN)
                .foregroundTintColor(Color.RED)
                .build()
        val locationComponentActivationOptions =
            buildLocationComponentActivationOptions(style, locationComponentOptions)
        locationComponent.activateLocationComponent(locationComponentActivationOptions)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING
        // now call locate banks to mark
        try { loadBanksInfo(3.0, 10) }
        catch (e: Exception) {
            // for now it is re calling loadBanks()
            loadBanksInfo(3.0, 10)
        }
    }

    private fun loadBanksInfo(distance: Double, limit: Int) {
        nearbyBankLocationViewModel.getNearbyBanksLiveData(
            requireContext(),
            "Bank",
            distance, limit,
            lastLocation!!.longitude, lastLocation!!.latitude
        ).observe(viewLifecycleOwner, Observer { banks: List<Place> ->
            createMarkersForBanks(banks)
        })
    }

    private fun buildLocationComponentActivationOptions(
        style: Style,
        locationComponentOptions: LocationComponentOptions
    ): LocationComponentActivationOptions {
        return LocationComponentActivationOptions
            .builder(requireContext(), style)
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

    private fun createMarkersForBanks(banks: List<Place>) {
        for (bank in banks) {
            val markerOptions = MarkerOptions()
                .position(
                    LatLng(
                        bank.latitude.toDouble(),
                        bank.longitude.toDouble()
                    )
                )
                .title(bank.name)

            val marker = mapboxMap.addMarker(markerOptions)
            bankMarkers[marker] = bank
            setMarkerClickEvent()
        }
    }

    // marker click event
    private fun setMarkerClickEvent() {
        mapboxMap.setOnMarkerClickListener { place ->
            val bank: Place? = bankMarkers[place]
            bank?.let {
                // now call another fragment and pass bank information
                val bundle: Bundle = Bundle()
                bundle.putString(Constants.BANK_DATA, Gson().toJson(bank))
                findNavController().navigate(R.id.action_mapFragment_to_infoWindowFragment, bundle)
            }

            true
        }
    }

    // lifecylce event
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
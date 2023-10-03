package com.mehedisoftdev.barikoimapapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.WellKnownTileServer
import com.mapbox.mapboxsdk.maps.Style
import com.mehedisoftdev.barikoimapapps.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Mapbox with your Barikoi API key
        val BARIKOI_API_KEY: String = getString(R.string.barikoi_api_key)
        val wellKnownTileServer = WellKnownTileServer.MapTiler

        Mapbox.getInstance(
            this, BARIKOI_API_KEY, wellKnownTileServer
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.getMapAsync { map ->
            map.setStyle(
                Style.Builder()
                    .fromUri("https://map.barikoi.com/styles/barikoi-bangla/style.json?key=${BARIKOI_API_KEY}")
            ) {}
        }


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
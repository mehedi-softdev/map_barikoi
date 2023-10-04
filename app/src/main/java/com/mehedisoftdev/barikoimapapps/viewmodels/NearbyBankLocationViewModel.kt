package com.mehedisoftdev.barikoimapapps.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mehedisoftdev.barikoimapapps.models.Place
import com.mehedisoftdev.barikoimapapps.repository.NearbyBankLocationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyBankLocationViewModel @Inject constructor(
    private val nearbyBankLocationRepo: NearbyBankLocationRepo
) : ViewModel() {
    fun getNearbyBanksLiveData(
        context: Context,
        distance: Double,
        limit: Int,
        lon: Double,
        lat: Double
    ): LiveData<List<Place>> {
        val data: MutableLiveData<List<Place>> = MutableLiveData()

        viewModelScope.launch {
            data.postValue(
                nearbyBankLocationRepo.getNearbyPlaces(context, distance, limit, lon, lat)
            )
        }

        val filteredBankData: LiveData<List<Place>> = data.map { places ->
            places.filter { it.pType.toString().lowercase() == "bank" }
        }
        return filteredBankData
    }
}
package com.mehedisoftdev.barikoimapapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mehedisoftdev.barikoimapapps.databinding.FragmentInfoWindowBinding
import com.mehedisoftdev.barikoimapapps.models.Place
import com.mehedisoftdev.barikoimapapps.utils.Constants
import java.math.RoundingMode
import java.text.DecimalFormat

class InfoWindowFragment : Fragment() {
    private var _binding: FragmentInfoWindowBinding? = null
    private val binding: FragmentInfoWindowBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoWindowBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // back button event
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val stringBank = arguments?.getString(Constants.BANK_DATA)
        if (stringBank != null) {
            val bank: Place = Gson().fromJson(stringBank, Place::class.java)
            bindViews(bank)
        } else {
            Toast.makeText(context, "Data fetching error!", Toast.LENGTH_SHORT)
                .show()
            findNavController().popBackStack()
        }

    }

    private fun bindViews(bank: Place) {
        binding.bankName.text = String.format(getString(R.string.bank_name), bank.name)
        binding.address.text = String.format(getString(R.string.address), bank.Address)
        binding.postCode.text = String.format(getString(R.string.post_code), bank.postCode)
        binding.subType.text = String.format(getString(R.string.sub_type), bank.subType)
        // formating meters distance
        val df = DecimalFormat("#.##")
        val distance = df.format(bank.distance_in_meters.toDouble())
        binding.distanceFromHome.text =
            String.format(getString(R.string.distance_from_home), distance)
        binding.coordinates.text =
            String.format(getString(R.string.coordinates), "(${bank.latitude}, ${bank.longitude})")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
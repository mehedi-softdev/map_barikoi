package com.mehedisoftdev.barikoimapapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mehedisoftdev.barikoimapapps.databinding.FragmentInfoWindowBinding

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

}
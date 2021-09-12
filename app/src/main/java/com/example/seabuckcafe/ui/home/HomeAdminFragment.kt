package com.example.seabuckcafe.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.databinding.FragmentHomeAdminBinding

class HomeAdminFragment: Fragment() {

    private var binding: FragmentHomeAdminBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.homeAdminFragment = this@HomeAdminFragment


    }

}
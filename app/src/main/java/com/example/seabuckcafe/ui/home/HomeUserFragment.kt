package com.example.seabuckcafe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentHomeUserBinding

class HomeUserFragment: Fragment() {

    private var binding: FragmentHomeUserBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeUserBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.homeUserFragment = this@HomeUserFragment

        //set appbar in custom fragment page
        binding?.topAppBar?.setNavigationIcon(R.drawable.ic_appbar_menu)
        binding?.topAppBar?.setNavigationOnClickListener {
            binding?.drawerLayout?.open()
        }

    }
}
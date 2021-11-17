package com.example.seabuckcafe.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.AdminTabPageAdapter
import com.example.seabuckcafe.databinding.FragmentAdminOrderListBinding
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.tabs.TabLayoutMediator

class AdminOrderListFragment: Fragment() {
    private lateinit var binding: FragmentAdminOrderListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminOrderListBinding.inflate(inflater, container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.topAppBar.setNavigationOnClickListener { backward() }

        tabLayoutMediator()
    }

    private fun tabLayoutMediator() {

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.viewPager2

        val adapter = AdminTabPageAdapter(requireActivity().supportFragmentManager, requireActivity().lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> { tab.text = getString(R.string.pending) }
                1 -> { tab.text = getString(R.string.on_prepare) }
                2 -> { tab.text = getString(R.string.delivery) }
            }
        }.attach()
    }

    private fun backward() {
        Utils().backward(this, R.id.homeAdminFragment)
    }

}
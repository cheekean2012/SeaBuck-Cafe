package com.example.seabuckcafe.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.seabuckcafe.ui.order.AdminOrderDeliveringListFragment
import com.example.seabuckcafe.ui.order.AdminOrderPendingListFragment
import com.example.seabuckcafe.ui.order.AdminOrderPrepareListFragment


class AdminTabPageAdapter(fragment: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> {
                AdminOrderPrepareListFragment()
            }
            2 -> {
                AdminOrderDeliveringListFragment()
            }
            else -> AdminOrderPendingListFragment()
        }
    }

}
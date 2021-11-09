package com.example.seabuckcafe.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentAdminOrderListBinding
import com.example.seabuckcafe.utils.Utils

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
        binding.pendingForward.setOnClickListener { goToPendingPage() }
        binding.prepareForward.setOnClickListener { goToPreparePage() }
        binding.deliveringForward.setOnClickListener { goToDeliveryPage() }
    }

    private fun goToDeliveryPage() {
        Utils().forward(this, R.id.action_adminOrderListFragment_to_adminOrderDeliveringListFragment)
    }

    private fun goToPreparePage() {
        Utils().forward(this, R.id.action_adminOrderListFragment_to_adminOrderPrepareListFragment)
    }

    private fun goToPendingPage() {
        Utils().forward(this, R.id.action_adminOrderListFragment_to_adminOrderPendingListFragment)
    }


    private fun backward() {
        Utils().backward(this, R.id.homeAdminFragment)
    }

}
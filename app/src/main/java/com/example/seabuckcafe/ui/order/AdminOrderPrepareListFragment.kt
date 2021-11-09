package com.example.seabuckcafe.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentAdminOrderPrepareBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.UserOrderList
import com.example.seabuckcafe.utils.Utils

class AdminOrderPrepareListFragment: Fragment() {

    private lateinit var binding: FragmentAdminOrderPrepareBinding
    private lateinit var orderList: MutableList<UserOrderList>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminOrderPrepareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderList = ArrayList()

        binding.lifecycleOwner = viewLifecycleOwner

        recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Firestore().getUserOrderListPrepare(this, recyclerView)

        binding.topAppBar.setNavigationOnClickListener { backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.adminOrderListFragment)
    }
}
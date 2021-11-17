package com.example.seabuckcafe.ui.order

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.databinding.FragmentAdminOrderPendingBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.UserOrderList

class AdminOrderPendingListFragment: Fragment() {

    private lateinit var binding: FragmentAdminOrderPendingBinding
    private lateinit var orderList: MutableList<UserOrderList>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminOrderPendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderList = ArrayList()
        binding.lifecycleOwner = viewLifecycleOwner

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Firestore().getUserOrderListPending(this, recyclerView)
        }

    }
}
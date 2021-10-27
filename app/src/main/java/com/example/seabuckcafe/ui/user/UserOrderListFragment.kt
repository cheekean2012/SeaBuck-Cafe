package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserOrderListBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.UserOrderList
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.FirebaseAuth

class UserOrderListFragment: Fragment() {
    private lateinit var binding: FragmentUserOrderListBinding
    private lateinit var orderList: MutableList<UserOrderList>
    private var auth = FirebaseAuth.getInstance()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserOrderListBinding.inflate(inflater, container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderList = ArrayList()

        binding.lifecycleOwner = viewLifecycleOwner

        recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Firestore().getUserOrderList(this, auth.uid!!, recyclerView,
            orderList as ArrayList<UserOrderList>)

        binding.topAppBar.setNavigationOnClickListener { backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.homeUserFragment)
    }
}
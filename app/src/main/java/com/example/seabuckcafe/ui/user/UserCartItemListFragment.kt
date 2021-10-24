package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.UserProductListItemAdapter
import com.example.seabuckcafe.databinding.FragmentUserCartItemListBinding
import com.example.seabuckcafe.models.ProductItem
import com.example.seabuckcafe.models.UserCartViewModel
import com.example.seabuckcafe.utils.Utils

class UserCartItemListFragment: Fragment() {

    private lateinit var binding: FragmentUserCartItemListBinding
    private val cartViewModel: UserCartViewModel by activityViewModels()
    private lateinit var  productItemList: LiveData<MutableList<ProductItem>>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserCartItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Array
        productItemList = cartViewModel.product
        binding.lifecycleOwner = viewLifecycleOwner

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = UserProductListItemAdapter(this, requireContext(), productItemList)

        cartViewModel.subTotal.observe(viewLifecycleOwner, Observer {
            binding.totalPrice.text = requireContext().getString(R.string.rm, cartViewModel.subTotal.value!!.toDouble())
        })


        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.continueButton.setOnClickListener { forward() }
    }

    private fun forward() {
        if (productItemList.value.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "You haven't add food item in cart!", Toast.LENGTH_SHORT).show()
        } else {
            Utils().forward(this, R.id.action_userCartItemListFragment_to_userPaymentFragment)
        }
    }

    private fun backward() {
        Utils().backward(this, R.id.foodItemListFragment)
    }
}
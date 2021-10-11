package com.example.seabuckcafe.ui.admin

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.AdminFoodListItemAdapter
import com.example.seabuckcafe.databinding.FragmentAdminFoodItemListBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.AdminMenuItem
import com.example.seabuckcafe.utils.Utils

class AdminFoodItemListFragment: Fragment() {

    private lateinit var binding: FragmentAdminFoodItemListBinding
    private lateinit var foodItemList: ArrayList<AdminMenuItem>
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminFoodItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.adminFoodItemListFragment = this@AdminFoodItemListFragment

        foodItemList = ArrayList()
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = AdminFoodListItemAdapter(requireContext(), foodItemList)
        Firestore().getFoodMenuItem(this, foodItemList, recyclerView )


        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.topAppBar.setOnMenuItemClickListener { menuItem -> optionsItemSelected(menuItem) }
    }

    private fun optionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> Utils().forward(this, R.id.action_adminFoodItemListFragment_to_adminFoodDetailFragment)
            R.id.filter -> Toast.makeText(requireContext(), "Item Clicked!", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun backward() {
        Utils().backward(this, R.id.homeAdminFragment)
    }

}
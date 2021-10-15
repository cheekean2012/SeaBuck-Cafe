package com.example.seabuckcafe.ui.admin

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.AdminFoodListItemAdapter
import com.example.seabuckcafe.adapters.FoodTypeListItemAdapter
import com.example.seabuckcafe.databinding.DialogCustomListBinding
import com.example.seabuckcafe.databinding.FragmentAdminFoodItemListBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.AdminMenuItem
import com.example.seabuckcafe.models.MenuSharedViewModel
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils

class AdminFoodItemListFragment: Fragment() {

    private lateinit var binding: FragmentAdminFoodItemListBinding
    private lateinit var foodItemList: MutableList<AdminMenuItem>
    private lateinit var recyclerView: RecyclerView
    private lateinit var mDialog: Dialog
    private val shareViewModel: MenuSharedViewModel by activityViewModels()

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

        // Initialize array list
        foodItemList = ArrayList()

        // Set recycler to UI controller, layout and adapter
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = AdminFoodListItemAdapter(this, requireContext(), foodItemList)

        // Get data from firebase
        Firestore().getFoodMenuItem(this, recyclerView)

        // Set top action bar item click
        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.topAppBar.setOnMenuItemClickListener { menuItem -> optionsItemSelected(menuItem) }
    }

    // Set the item click listener from top action bar
    private fun optionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                shareViewModel.resetValue()
                Utils().forward(this, R.id.action_adminFoodItemListFragment_to_adminFoodDetailFragment)
            }
            R.id.filter -> foodTypeDialog(resources.getString(R.string.select_food_type), Constants.foodType())
        }
        return false
    }

    // Back to previous layout
    private fun backward() {
        Utils().backward(this, R.id.homeAdminFragment)
    }

    // Create food type dialog in recyclerview
    private fun foodTypeDialog(title: String, itemList: List<String>) {

        mDialog = Dialog(requireContext())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mDialog.setContentView(binding.root)

        // Set title
        binding.dialogFoodTitle.text = title

        binding.dialogFoodRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FoodTypeListItemAdapter(requireContext(), itemList)
        binding.dialogFoodRecyclerView.adapter = adapter
        mDialog.show()

        // After clicked get position and the food type from recyclerview
        adapter.setOnItemClickListener(object : FoodTypeListItemAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, foodType: String) {
                Log.d("item", "current clicked is $foodType")
                Firestore().filterFoodMenuItemType(this@AdminFoodItemListFragment, foodType, recyclerView)
                mDialog.dismiss()
            }
        })
    }
}
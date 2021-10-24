package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.UserFoodListItemAdapter
import com.example.seabuckcafe.databinding.FragmentUserFoodItemListBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.MenuSharedViewModel
import com.example.seabuckcafe.models.UserCartViewModel
import com.example.seabuckcafe.models.UserMenuItem
import com.example.seabuckcafe.utils.Utils

class UserFoodItemListFragment : Fragment() {

    private lateinit var binding: FragmentUserFoodItemListBinding
    private lateinit var foodItemList: MutableList<UserMenuItem>
    private lateinit var recyclerView: RecyclerView
    private val sharedViewModel: MenuSharedViewModel by activityViewModels()
    private val cartViewModel: UserCartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserFoodItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userFoodItemListFragment = this@UserFoodItemListFragment

        // Initialize array list
        foodItemList = ArrayList()

        // Set recycler to UI controller, layout and adapter
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = UserFoodListItemAdapter(this, requireContext(), foodItemList)

        // Get data from firebase
        Firestore().userGetFoodMenuItem(this, sharedViewModel.type.value.toString(), recyclerView)

        // Check notification on badge
        badgeNotificationVisible()

        binding.visibleBadge.cartIcon.setOnClickListener { goToCart() }
        binding.topAppBar.setNavigationOnClickListener{ backward() }
    }

    private fun goToCart() {
        Utils().forward(this, R.id.action_foodItemListFragment_to_userCartItemListFragment)
    }

    // Set notification on badge and set item count when user clicked add to cart
    private fun badgeNotificationVisible() {

        if (cartViewModel.isQuantityZero.value != true) {
            binding.visibleBadge.notificationNumberContainer.visibility = View.INVISIBLE
        } else {
            binding.visibleBadge.notificationNumberContainer.visibility = View.VISIBLE
            binding.visibleBadge.numberCount.text = cartViewModel.quantity.value.toString()
        }
    }


    private fun backward() {
        Utils().backward(this, R.id.homeUserFragment)
    }
}
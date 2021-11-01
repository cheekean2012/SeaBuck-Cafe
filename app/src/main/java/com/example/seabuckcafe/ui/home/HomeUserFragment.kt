package com.example.seabuckcafe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentHomeUserBinding
import com.example.seabuckcafe.models.MenuSharedViewModel
import com.example.seabuckcafe.models.UserCartViewModel
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeUserFragment: Fragment() {

    private lateinit var binding: FragmentHomeUserBinding
    private val sharedViewModel: MenuSharedViewModel by activityViewModels()
    private val cartViewModel: UserCartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeUserBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            homeUserFragment = this@HomeUserFragment

            // Set action bar in custom fragment page
            topAppBar.setNavigationIcon(R.drawable.ic_appbar_menu)
            topAppBar.setNavigationOnClickListener {
                drawerLayout.open()
            }
            // Set drawer layout navigation view
            navView.setNavigationItemSelectedListener{ navDrawerNavigation(it) }
            visibleBadge.cartIcon.setOnClickListener { goToCart() }
        }

        // Check notification on badge
        badgeNotificationVisible()
    }

    private fun navDrawerNavigation(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.profile -> {
                Utils().forward(this, R.id.action_homeUserFragment_to_userProfileFragment)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.order -> {
                Utils().forward(this, R.id.action_homeUserFragment_to_userOrderListFragment)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.about_us -> {
                Utils().forward(this, R.id.action_homeUserFragment_to_userAboutFragment)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.logout -> {
                Firebase.auth.signOut()
                findNavController().navigate(R.id.action_homeUserFragment_to_loginFragment)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            else -> return false
        }
    }

    fun forwardToListFoodItem(type : String) {
        sharedViewModel.setType(type)
        Utils().forward(this, R.id.action_homeUserFragment_to_foodItemListFragment)
    }

    private fun goToCart() {
        Utils().forward(this, R.id.action_homeUserFragment_to_userCartItemListFragment)
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

}
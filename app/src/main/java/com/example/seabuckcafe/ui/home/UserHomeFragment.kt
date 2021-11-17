package com.example.seabuckcafe.ui.home

import android.os.Build
import android.os.Bundle
import android.view.*
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

class UserHomeFragment: Fragment() {

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

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            )
        }

        binding.apply {
            userHomeFragment = this@UserHomeFragment

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
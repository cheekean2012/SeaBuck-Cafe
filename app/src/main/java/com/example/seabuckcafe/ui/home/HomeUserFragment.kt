package com.example.seabuckcafe.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentHomeUserBinding
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeUserFragment: Fragment() {

    private lateinit var binding: FragmentHomeUserBinding

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
        binding.homeUserFragment = this@HomeUserFragment

        //set appbar in custom fragment page
        binding.topAppBar.setNavigationIcon(R.drawable.ic_appbar_menu)
        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.navView.setNavigationItemSelectedListener{ menuItem ->

           when (menuItem.itemId) {
               R.id.profile -> {
                   Utils().forward(this, R.id.action_homeUserFragment_to_userProfileFragment)
                   binding.drawerLayout.closeDrawer(GravityCompat.START)
                   true
               }
               R.id.order -> {
                   true
               }
               R.id.about_us -> {
                   true
               }
               R.id.logout -> {
                   Firebase.auth.signOut()
                   findNavController().navigate(R.id.action_homeUserFragment_to_loginFragment)
                   binding.drawerLayout.closeDrawer(GravityCompat.START)
                   true
               }
               else -> false
           }
        }
    }

}
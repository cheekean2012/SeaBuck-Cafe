package com.example.seabuckcafe.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentHomeAdminBinding
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeAdminFragment: Fragment() {

    private lateinit var binding: FragmentHomeAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeAdminFragment = this@HomeAdminFragment
    }

    fun backToLogin() {
        Firebase.auth.signOut()
        Utils().forward(this, R.id.action_homeAdminFragment_to_loginFragment)
    }

    fun goToMenu() {
        Utils().forward(this, R.id.action_homeAdminFragment_to_adminFoodItemListFragment)
    }
}
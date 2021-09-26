package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserProfileBinding
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_MINI

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userProfileFragment = this@UserProfileFragment

        // Set fab button size mini
        binding.floatingCameraButton.size = SIZE_MINI
        binding.topAppBar.setNavigationOnClickListener{ backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.homeUserFragment)
    }

    fun goToAccount() {
        Utils().forward(this, R.id.action_userProfile_to_userAccountFragment)
    }

    fun goToEmailAddress() {
        Utils().forward(this, R.id.action_userProfileFragment_to_userAddressFragment)
    }

}
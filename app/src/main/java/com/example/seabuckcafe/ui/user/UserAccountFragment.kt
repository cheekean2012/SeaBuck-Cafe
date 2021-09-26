package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserAccountBinding
import com.example.seabuckcafe.utils.Utils


class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userAccountFragment = this@UserAccountFragment

        binding.topAppBar.setNavigationOnClickListener { backward() }
    }

    fun backward() {
        Utils().backward(this, R.id.userProfileFragment)
    }

}
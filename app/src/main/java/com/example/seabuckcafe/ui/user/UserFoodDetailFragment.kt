package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserFoodDetailBinding
import com.example.seabuckcafe.utils.Utils


class UserFoodDetailFragment : Fragment() {

    private lateinit var binding: FragmentUserFoodDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userFoodDetailFragment = this@UserFoodDetailFragment

        binding.topAppBar.setNavigationOnClickListener{ backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.foodItemListFragment)
    }

}
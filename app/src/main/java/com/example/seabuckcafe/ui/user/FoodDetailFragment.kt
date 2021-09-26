package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentFoodDetailBinding
import com.example.seabuckcafe.utils.Utils


class FoodDetailFragment : Fragment() {

    private lateinit var binding: FragmentFoodDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.foodDetailFragment = this@FoodDetailFragment

        binding.topAppBar.setNavigationOnClickListener{ backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.foodItemListFragment)
    }

}
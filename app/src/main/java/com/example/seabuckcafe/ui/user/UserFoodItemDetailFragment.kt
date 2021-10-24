package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserFoodInDetailBinding
import com.example.seabuckcafe.models.MenuSharedViewModel
import com.example.seabuckcafe.models.ProductItem
import com.example.seabuckcafe.models.UserCartViewModel
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils
import java.text.NumberFormat
import java.util.*


class UserFoodDetailFragment : Fragment() {

    private lateinit var binding: FragmentUserFoodInDetailBinding
    private val sharedViewModel: MenuSharedViewModel by activityViewModels()
    private val cartViewModel: UserCartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserFoodInDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            userFoodDetailFragment = this@UserFoodDetailFragment

            viewModel = sharedViewModel

            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            Glide.with(requireContext())
                .load(sharedViewModel.image.value)
                .into(foodImage)

            foodDescription.text = textOutput()

            topAppBar.setNavigationOnClickListener{ backward() }
        }
    }

    private fun textOutput(): CharSequence {

        val priceFormat = NumberFormat.getNumberInstance().format(sharedViewModel.price.value!!.toDouble())

        return  resources.getString(R.string.food_type) + ": ${sharedViewModel.type.value} \n" +
                resources.getString(R.string.food_price) + ": ${resources.getString(R.string.rm, priceFormat.toDouble())} \n" +
                resources.getString(R.string.food_description) + ": ${sharedViewModel.description.value}"
    }

    private fun backward() {
        Utils().backward(this, R.id.foodItemListFragment)
    }

    fun addCart() {
        val uniqueID = UUID.randomUUID().toString()

        val item = ProductItem(
            uniqueID,
            sharedViewModel.image.value.toString(),
            sharedViewModel.title.value.toString(),
            1,
            sharedViewModel.price.value.toString()
        )
        cartViewModel.setProduct(item)
        cartViewModel.addSubTotal(item.price.toDouble())
        cartViewModel.badgeQuantityIncrease(Constants.QUANTITY_INCREASE)

        Toast.makeText(requireContext(), "Item has been added to cart", Toast.LENGTH_SHORT).show()
        backward()
    }
}
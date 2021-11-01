package com.example.seabuckcafe.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserOrderDetailBinding
import com.example.seabuckcafe.models.UserOrderViewModel
import com.example.seabuckcafe.utils.Utils
import java.text.NumberFormat

class UserOrderDetailFragment: Fragment() {

    private lateinit var binding: FragmentUserOrderDetailBinding
    private val userOrderViewModel: UserOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOrderText()
        binding.topAppBar.setNavigationOnClickListener { backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.userOrderListFragment)
    }

    private fun setOrderText() {
        userOrderViewModel.apply {

            val foodItem = orderProduct.value?.joinToString(separator = "\n") {
                "${it.itemName} x ${it.quantity}"
            }

            val reasonNotEmpty = reason.value.equals("")

            val priceFormat = NumberFormat.getNumberInstance().format(totalPrice.value?.toDouble())

            binding.foodDescription.text =
                getString(R.string.payment_type, paymentType.value.toString()) + "\n" +
                getString(R.string.pickup_type, pickupType.value.toString()) + "\n\n" +
                "Food\n" + foodItem + "\n" +
                getString(R.string.total_price, getString(R.string.rm, priceFormat.toString().toDouble()))

            if (!reasonNotEmpty) {
                binding.apply {
                    reasonToCancel.visibility = View.VISIBLE
                    reasonText.visibility = View.VISIBLE
                    reasonText.text = reason.value
                }
            }
        }
    }
}
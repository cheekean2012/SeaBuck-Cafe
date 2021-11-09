package com.example.seabuckcafe.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentAdminReportDetailBinding
import com.example.seabuckcafe.models.UserOrderViewModel
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils

class AdminReportDetailFragment: Fragment() {

    private lateinit var binding: FragmentAdminReportDetailBinding
    private val userOrderViewModel: UserOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminReportDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOrderText()
        binding.topAppBar.setNavigationOnClickListener { backward() }
    }

    private fun setOrderText() {
        userOrderViewModel.apply {

            binding.apply {
                orderUserName.text = getString(R.string.order_name, name.value.toString())
                orderPhoneNumber.text = getString(R.string.phone_number_order, phoneNumber.value.toString())
                orderPayment.text = getString(R.string.payment_type, paymentType.value.toString())
                orderPickup.text = getString(R.string.pickup_type, pickupType.value.toString())

                val foodItem = orderProduct.value?.joinToString(separator = "\n") {
                    "${it.itemName} x ${it.quantity}"
                }

                val foodPrice = orderProduct.value?.joinToString(separator = "\n") {
                    "RM${String.format("%.2f", it.price.toFloat())}"
                }

                orderFoodItem.text = foodItem
                orderFoodPrice.text = foodPrice

                val status = status.value.toString()
                if (status == Constants.STATUS_CANCEL) {
                    cancelReason.visibility = View.VISIBLE
                    reasonScroll.visibility = View.VISIBLE
                    reasonText.text = reason.value.toString()
                }

            }
        }
    }

    private fun backward() {
        Utils().backward(this, R.id.adminReportListFragment)
    }
}
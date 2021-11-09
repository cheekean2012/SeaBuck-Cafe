package com.example.seabuckcafe.ui.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentAdminOrderDetailBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.UserOrderViewModel
import com.example.seabuckcafe.services.NotificationService
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText


class AdminOrderDetailFragment: Fragment() {

    private lateinit var binding: FragmentAdminOrderDetailBinding
    private val userOrderViewModel: UserOrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val status = userOrderViewModel.status.value.toString()

        Log.d("status", "$status")
        if (status != Constants.STATUS_PENDING) {
            binding.orderCancel.isClickable = false
        }

        setOrderText()
        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.orderAccept.setOnClickListener { updateOrderInfo() }
        binding.orderCancel.setOnClickListener { cancelOrder() }
    }

    private fun cancelOrder() {
        // Get add address item layout
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_order_cancel, null)

        // Get edit text id
        val reason = inflater.findViewById<TextInputEditText>(R.id.cancelEditText)

        // Create dialog
        MaterialAlertDialogBuilder(requireContext())
            .setView(inflater)
            .setPositiveButton("OK") {
                    dialog, _ ->
                val cancelReason = reason.text.toString()
                userOrderViewModel.setStatus(Constants.STATUS_CANCEL)
                val status = userOrderViewModel.status.value.toString()
                val id = userOrderViewModel.id.value.toString()

                Firestore().cancelUserOrderList(requireContext(), status, cancelReason, id)

                backward()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") {
                    dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun updateOrderInfo() {

        var status = userOrderViewModel.status.value.toString()
        val id = userOrderViewModel.id.value.toString()
        val pickup = userOrderViewModel.pickupType.value.toString()

        userOrderViewModel.apply {

            if (status == Constants.STATUS_DELIVERING) {
                status = Constants.STATUS_COMPLETE
                binding.orderAccept.isClickable = false
            }

            if (status == Constants.STATUS_ON_PREPARE) {

                if (pickup == "Delivery") {
                    status = Constants.STATUS_DELIVERING

                } else {
                    status = Constants.STATUS_COMPLETE
                    binding.orderAccept.isClickable = false
                }
                binding.orderAccept.text = getString(R.string.complete)
            }

            if (status == Constants.STATUS_PENDING) {

                Intent(context, NotificationService::class.java).also { intent ->
                    requireContext().stopService(intent) }

                if (pickup != "Self-Pickup") {
                    binding.orderAccept.text = getString(R.string.delivery)

                } else {
                    binding.orderAccept.text = getString(R.string.complete)
                }

                status = Constants.STATUS_ON_PREPARE
                binding.orderCancel.isClickable = false

            }

        }
        userOrderViewModel.setStatus(status)
        Firestore().updateUserOrderList(this, status, id)
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
                val pickup = userOrderViewModel.pickupType.value.toString()

                if (status == Constants.STATUS_ON_PREPARE && pickup == "Delivery") {

                    orderAccept.text = getString(R.string.delivery)

                } else if (status == Constants.STATUS_ON_PREPARE && pickup == "Self-Pickup") {

                    orderAccept.text = getString(R.string.complete)

                }else if (status == Constants.STATUS_DELIVERING) {

                    orderAccept.text = getString(R.string.complete)
                }

            }
        }
    }

    private fun backward() {
        Utils().backward(this, R.id.adminOrderListFragment)
    }

}
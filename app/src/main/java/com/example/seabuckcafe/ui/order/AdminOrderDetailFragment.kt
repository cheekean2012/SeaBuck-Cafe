package com.example.seabuckcafe.ui.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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

        setOrderText()

        binding.apply {

            topAppBar.setNavigationOnClickListener { backward() }

            if (status != Constants.STATUS_COMPLETE) {
                orderAccept.setOnClickListener { updateOrderInfo() }
            } else {
                orderAccept.isClickable = false
            }

            if (status == Constants.STATUS_PENDING) {
                orderCancel.setOnClickListener { cancelOrder() }
            } else {
                orderCancel.isClickable = false
            }
        }
    }

    private fun cancelOrder() {
        // Get add address item layout
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_order_cancel, null)

        // Get edit text id
        val reason: EditText = inflater.findViewById(R.id.cancelEditText)

        // Create dialog
        MaterialAlertDialogBuilder(requireContext())
            .setView(inflater)
            .setPositiveButton("OK") {
                    dialog, _ ->

                val cancelReason = reason.text.toString().trim{ it <= ' '}

                if (cancelReason.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter your reason", Toast.LENGTH_SHORT).show()
                } else {
                    userOrderViewModel.setStatus(Constants.STATUS_CANCEL)
                    val status = userOrderViewModel.status.value.toString()
                    val id = userOrderViewModel.id.value.toString()

                    Intent(context, NotificationService::class.java).also { intent ->
                        requireContext().stopService(intent) }

                    Firestore().cancelUserOrderList(requireContext(), status, cancelReason, id)

                    backward()
                }

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
                backward()
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
        Firestore().updateUserOrderList(this, requireContext(), status, id)
    }

    private fun setOrderText() {
        userOrderViewModel.apply {

            binding.apply {
                orderUserName.text = getString(R.string.order_name, name.value.toString())
                orderPhoneNumber.text = getString(R.string.phone_number_order, phoneNumber.value.toString())
                orderPayment.text = getString(R.string.payment_type, paymentType.value.toString())
                orderPickup.text = getString(R.string.pickup_type, pickupType.value.toString())
                orderAddress.text = getString(R.string.order_address, address.value.toString())

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
        Utils().forward(this, R.id.action_adminOrderDetailFragment_to_adminOrderListFragment)
    }

}
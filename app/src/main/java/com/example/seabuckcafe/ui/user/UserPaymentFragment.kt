package com.example.seabuckcafe.ui.user

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.UserPaymentAddressListAdapter
import com.example.seabuckcafe.databinding.DialogAddressListBinding
import com.example.seabuckcafe.databinding.FragmentUserPaymentBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.PersonalInfoViewModel
import com.example.seabuckcafe.models.UserAddressList
import com.example.seabuckcafe.models.UserCartViewModel
import com.example.seabuckcafe.models.UserOrderList
import com.example.seabuckcafe.utils.Constants
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class UserPaymentFragment: Fragment() {
    private lateinit var binding: FragmentUserPaymentBinding
    private val cartViewModel: UserCartViewModel by activityViewModels()
    private val personalInfoViewModel: PersonalInfoViewModel by activityViewModels()
    private lateinit var  userAddressList: MutableList<UserAddressList>
    private var auth = FirebaseAuth.getInstance()
    private lateinit var mDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = cartViewModel

            Firestore().getOneUserAddress(this@UserPaymentFragment, auth.uid!!)

            topAppBar.setNavigationOnClickListener { backward() }
            changeBtn.setOnClickListener { dialogAddress(resources.getString(R.string.select_address)) }
            confirmPayment.setOnClickListener { confirmPayment() }
            pickupGroup.setOnCheckedChangeListener { radioGroup, checkId -> showPaymentMethod(radioGroup, checkId) }
            paymentGroup.setOnCheckedChangeListener { radioGroup, checkedId -> showCardInput(radioGroup, checkedId) }
        }
    }

    private fun confirmPayment() {

        val checkInfo = isValidInfo()

        if (!checkInfo) {
            Toast.makeText(requireContext(), "Please fill correct your payment info", Toast.LENGTH_SHORT).show()
        } else {

            // Get current date & time
            val timeStamp = com.google.firebase.Timestamp.now()
            val currentDateTime = Calendar.getInstance()
            val dayOfMonth = currentDateTime.get(Calendar.DAY_OF_MONTH)
            val month = currentDateTime.get(Calendar.MONTH) + 1
            val year = currentDateTime.get(Calendar.YEAR)
            val time = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("HH:mm").format(time)

            Log.d("date checked", "$dayOfMonth $month $year, $formatter")

            val userId = auth.uid!!

            val order = UserOrderList(
                "",
                userId,
                timeStamp,
                dayOfMonth.toString(),
                month.toString(),
                year.toString(),
                formatter,
                personalInfoViewModel.name.value.toString(),
                personalInfoViewModel.phoneNumber.value.toString(),
                cartViewModel.product.value,
                cartViewModel.pickType.value.toString(),
                cartViewModel.paymentType.value.toString(),
                cartViewModel.subTotal.value.toString(),
                Constants.STATUS_PENDING,
                ""
            )
            Firestore().addUserOrderList(requireContext(), order)

            cartViewModel.clearProduct()

            Utils().backward(this, R.id.homeUserFragment)
        }
    }

    private fun isValidInfo(): Boolean {

        val pickupGroup = binding.pickupGroup.checkedRadioButtonId

        if (pickupGroup == -1) {
            return false
        }

        val paymentGroup = binding.paymentGroup.checkedRadioButtonId
        if (paymentGroup == -1) {
            return false
        }

        val paymentCard = binding.cardBtn.isChecked
        val cardNumber = binding.cardNumberEditText.text
        val cardCVC = binding.cardNumberCVCEditText.text

        if (paymentCard && cardNumber.isNullOrEmpty()) {
            binding.cardNumberLayout.error = "Please input your card number"
            return false
        }

        if (paymentCard && cardCVC.isNullOrEmpty()) {
            binding.cardNumberCVCLayout.error = "Please input your card CVC"
            return false
        }

        if (paymentCard && cardNumber.toString().length != 16) {
            binding.cardNumberLayout.error = "Please enter 16 digits"
            return false
        }

        if (paymentCard && cardCVC.toString().length != 3) {
            binding.cardNumberCVCLayout.error = "Please enter 3 digits"
            return false
        }

        return true
    }

    // checked the pickup type to show payment method
    private fun showPaymentMethod(radioGroup: RadioGroup?, checkId: Int) {

        binding.apply {
            when (checkId) {
                R.id.selfPickupBtn -> {
                    paymentType.visibility = View.VISIBLE
                    paymentGroup.clearCheck()
                    cashBtn.visibility = View.INVISIBLE
                    cardBtn.visibility = View.VISIBLE
                }
                R.id.deliveryBtn -> {
                    paymentType.visibility = View.VISIBLE
                    paymentGroup.clearCheck()
                    cashBtn.visibility = View.VISIBLE
                    cardBtn.visibility = View.VISIBLE
                }
            }
        }
    }

    // checked the payment type to show card input
    private fun showCardInput(radioGroup: RadioGroup?, checkedId: Int) {

        binding.apply {
            when (checkedId) {
                R.id.cardBtn -> {
                    cardNumber.visibility = View.VISIBLE
                    cardNumberLayout.visibility = View.VISIBLE
                    cardNumberCVCLayout.visibility = View.VISIBLE
                }
                R.id.cashBtn -> {
                    cardNumber.visibility = View.INVISIBLE
                    cardNumberLayout.visibility = View.INVISIBLE
                    cardNumberCVCLayout.visibility = View.INVISIBLE
                    cardNumberEditText.text = null
                    cardNumberCVCEditText.text = null
                }
            }
        }
    }

    private fun dialogAddress(title: String) {

        userAddressList = ArrayList()

        mDialog = Dialog(requireContext())
        val binding: DialogAddressListBinding = DialogAddressListBinding.inflate(layoutInflater)
        mDialog.setContentView(binding.root)

        // Set title
        binding.dialogAddressTitle.text = title
        binding.dialogAddressRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        Firestore().getUserAddress(this, auth.uid!!, binding.dialogAddressRecyclerView, userAddressList)
        mDialog.show()
    }

    fun selectAddress(adapter: UserPaymentAddressListAdapter) {
        adapter.setOnItemClickListener(object : UserPaymentAddressListAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, address: String) {
                binding.addressView.text = address
                mDialog.dismiss()
            }

        })
    }

    private fun backward() {
        Utils().backward(this, R.id.userCartItemListFragment)
    }

    fun setAddress(address: MutableList<UserAddressList>) {
        if (address.isNotEmpty()) {
            binding.addressView.text = address[0].address
        }
    }
}
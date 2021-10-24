package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserPaymentBinding
import com.example.seabuckcafe.utils.Utils

class UserPaymentFragment: Fragment() {
    private lateinit var binding: FragmentUserPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.paymentGroup.setOnCheckedChangeListener { radioGroup, checkedId -> showCardInput(radioGroup, checkedId) }
    }

    private fun showCardInput(radioGroup: RadioGroup?, checkedId: Int) {

        when (checkedId) {
            R.id.cardBtn -> {
                binding.cardNumber.visibility = View.VISIBLE
                binding.cardNumberLayout.visibility = View.VISIBLE
            }
            R.id.cashBtn -> {
                binding.cardNumber.visibility = View.INVISIBLE
                binding.cardNumberLayout.visibility = View.INVISIBLE
                binding.cardNumberEditText.text = null
            }
        }
    }

    private fun backward() {
        Utils().backward(this, R.id.userCartItemListFragment)
    }

}
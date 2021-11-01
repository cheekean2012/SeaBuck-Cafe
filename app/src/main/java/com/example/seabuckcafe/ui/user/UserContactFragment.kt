package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserContactBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.utils.Utils

class UserContactFragment: Fragment() {
    private lateinit var binding: FragmentUserContactBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firestore().getAdminContactInfo(this)

        binding.topAppBar.setNavigationOnClickListener { backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.userProfileFragment)
    }

    fun getCafeInfo(info: Map<String, Any>?) {


        val address = info?.get("address").toString()
        val phone = info?.get("phoneNumber").toString()

        binding.contactDescription.text = getString(R.string.address).uppercase() + "\n" + address + "\n\n" +
                                          getString(R.string.phone_number).uppercase() + "\n" + phone
    }
}
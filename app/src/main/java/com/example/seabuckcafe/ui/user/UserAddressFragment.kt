package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.UserAddressAdapter
import com.example.seabuckcafe.databinding.FragmentUserAddressBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.UserAddressList
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class UserAddressFragment : Fragment() {

    private lateinit var binding: FragmentUserAddressBinding
    private lateinit var  userAddressList: MutableList<UserAddressList>
    private lateinit var recyclerView: RecyclerView
    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userAddressFragment = this@UserAddressFragment

        binding.topAppBar.setNavigationOnClickListener { backward() }

        userAddressList = ArrayList()

        recyclerView = binding.userAddressRecyclerView
        // Declare recyclerView in linear layout manager with context
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Firestore().getUserAddress(this, auth.uid!!, recyclerView, userAddressList)

        binding.userAddressAddButton.setOnClickListener { addInfo() }

    }

    private fun addInfo() {
        // Get add address item layout
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_address_item, null)

        // Get edit text id
        val newAddress: EditText = inflater.findViewById(R.id.newAddressEditText)

        // Create dialog
        MaterialAlertDialogBuilder(requireContext())
            .setView(inflater)
            .setPositiveButton("OK") {
                dialog, _ ->
                val address = newAddress.text.toString().trim{ it <= ' '}

                if (address.isEmpty()) {
                    Toast.makeText(requireContext(), "Make sure you have enter address", Toast.LENGTH_SHORT).show()
                } else {
                    // Add data into useAddressList
                    userAddressList.add(UserAddressList("", address))

                    val userAddress = UserAddressList(
                        "",
                        address
                    )
                    Firestore().addUserAddress(this, userAddress, auth.uid!!)

                    // Save the data into adapter and display to recyclerView
                    recyclerView.adapter = UserAddressAdapter(this, requireContext(), userAddressList)
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

    private fun backward() {
        Utils().backward(this, R.id.userProfileFragment)
    }

}
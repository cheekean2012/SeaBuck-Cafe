package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.adapters.UserAddressAdapter
import com.example.seabuckcafe.databinding.FragmentUserAddressBinding
import com.example.seabuckcafe.models.UserAddressData
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText


class UserAddressFragment : Fragment() {

    private lateinit var binding: FragmentUserAddressBinding
    private lateinit var userAddressList: ArrayList<UserAddressData>
    private lateinit var recyclerView: RecyclerView

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

        // Short form version from requireActivity().findViewById<RecyclerView>(R.id.userAddressRecyclerView)
        recyclerView = binding.userAddressRecyclerView

        // Declare recyclerView in linear layout manager with context
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get adapter data form UserAddressAdapter class
        recyclerView.adapter = UserAddressAdapter(requireContext(), userAddressList)

        binding.userAddressAddButton.setOnClickListener { addInfo() }

    }

    private fun addInfo() {
        // Get add address item layout
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_address_item, null)

        // Get edit text id
        val newAddress = inflater.findViewById<TextInputEditText>(R.id.newAddressEditText)

        // Create dialog
        MaterialAlertDialogBuilder(requireContext())
            .setView(inflater)
            .setPositiveButton("OK") {
                dialog, _ ->
                val address = newAddress.text.toString()
                // Add data into useAddressList
                userAddressList.add(UserAddressData("Address: $address"))

                // Save the data into adapter and display to recyclerView
                recyclerView.adapter?.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Added successful!", Toast.LENGTH_SHORT).show()
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
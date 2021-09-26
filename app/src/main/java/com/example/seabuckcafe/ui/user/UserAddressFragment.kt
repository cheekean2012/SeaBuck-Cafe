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

        //recyclerView = requireActivity().findViewById<RecyclerView>(R.id.userAddressRecyclerView)
        recyclerView = binding.userAddressRecyclerView

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = UserAddressAdapter(requireContext(), userAddressList)

        binding.userAddressAddButton.setOnClickListener { addInfo() }

    }

    private fun addInfo() {
        val inflater = LayoutInflater.from(requireContext())
            .inflate(R.layout.add_address_item, null)

        val newAddress = inflater.findViewById<TextInputEditText>(R.id.newAddressEditText)

        MaterialAlertDialogBuilder(requireContext())
            .setView(inflater)
            .setPositiveButton("OK") {
                _, _ ->
                val address = newAddress.text.toString()
                userAddressList.add(UserAddressData("Address: $address"))
                recyclerView.adapter?.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Added successful!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") {
                _, _ ->
            }
            .create()
            .show()

    }

    private fun backward() {
        Utils().backward(this, R.id.userProfileFragment)
    }

}
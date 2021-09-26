package com.example.seabuckcafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.models.UserAddressData

class UserAddressAdapter(
    private val context: Context,
    private val userAddressList: ArrayList<UserAddressData>): RecyclerView.Adapter<UserAddressAdapter.UserAddressViewHolder>() {

    inner class UserAddressViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val userAddress: TextView = view.findViewById<TextView>(R.id.addressSubTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAddressViewHolder {
        val userAddressLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_user_address_item, parent, false)
        return UserAddressViewHolder(userAddressLayout)
    }

    override fun onBindViewHolder(holder: UserAddressViewHolder, position: Int) {
        val item = userAddressList[position]
        holder.userAddress.text = item.address
    }

    override fun getItemCount(): Int {
        return userAddressList.size
    }
}
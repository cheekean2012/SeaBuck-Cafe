package com.example.seabuckcafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.models.UserAddressList

class UserPaymentAddressListAdapter(
    private val activity: Fragment,
    private val context: Context,
    private val userAddressList: MutableList<UserAddressList>): RecyclerView.Adapter<UserPaymentAddressListAdapter.UserPaymentAddressViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int, address: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    inner class UserPaymentAddressViewHolder(val view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        var userAddress: TextView = view.findViewById(R.id.addressSubTitleInPayment)
        private val cardView: CardView = view.findViewById(R.id.cartViewInPayment)

        init {
            cardView.setOnClickListener {
                val item = userAddressList[adapterPosition]
                listener.onItemClick(adapterPosition, item.address)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPaymentAddressViewHolder {

        // Get list user address layout
        val userAddressLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_user_address_in_payment, parent, false)
        return UserPaymentAddressViewHolder(userAddressLayout, mListener)
    }

    override fun onBindViewHolder(holder: UserPaymentAddressViewHolder, position: Int) {
        val item = userAddressList[position]

        holder.userAddress.text = item.address
    }

    override fun getItemCount(): Int {
        return userAddressList.size
    }
}
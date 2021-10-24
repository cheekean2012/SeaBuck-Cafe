package com.example.seabuckcafe.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.UserAddressData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class UserAddressAdapter(
    private val activity: Fragment,
    private val context: Context,
    private val userAddressList: MutableList<UserAddressData>): RecyclerView.Adapter<UserAddressAdapter.UserAddressViewHolder>() {

    inner class UserAddressViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        var userAddress: TextView = view.findViewById(R.id.addressSubTitle)
        private var mMenus: Button = view.findViewById(R.id.buttonMore)

        init {
            mMenus.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(view: View) {

            // Get current adapter position when user clicked menu in recycler view layout
            val position = userAddressList[adapterPosition]

            Log.d("get position address id", "${position.id} \n ${position.address}")

            // Create popup menu layout
            val popupMenus = PopupMenu(context.applicationContext, view)
            popupMenus.inflate(R.menu.edit_menu)

            // Set drawable icon if SDK greater than 30
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenus.setForceShowIcon(true)
            }

            // Set popup menu click event
            popupMenus.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.editText -> {
                        // Get and reuse add address item layout
                        val inflater = LayoutInflater.from(context).inflate(R.layout.dialog_add_address_item, null)
                        // Change add new address to edit address
                        inflater.findViewById<TextView>(R.id.addressTitle).setText(R.string.edit_address)

                        // Get edit text id
                        val newAddress = inflater.findViewById<TextInputEditText>(R.id.newAddressEditText)

                        // Create dialog for edit address
                        MaterialAlertDialogBuilder(context)
                            .setView(inflater)
                            .setPositiveButton("Save") {
                                dialog, _ ->
                                position.address = "Address: " + newAddress.text.toString()

                                // Specify the item when data has been changed and display
                                notifyItemChanged(adapterPosition)
                                Toast.makeText(context,"Edited Successful!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") {
                                dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.deleteText -> {

                        // Create dialog for delete
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Are you sure want to delete?")
                            .setPositiveButton("Yes") {
                                dialog, _ ->
                                // Remove data from specify position
                                userAddressList.removeAt(adapterPosition)
                                // After remove and display
                                notifyItemRemoved(adapterPosition)
                                Firestore().deleteUserAddress(activity, position.id!!)
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") {
                                dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }

                    else -> true
                }
            }
            popupMenus.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAddressViewHolder {

        // Get list user address layout
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
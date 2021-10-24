package com.example.seabuckcafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.ListUserCartItemBinding
import com.example.seabuckcafe.models.ProductItem
import com.example.seabuckcafe.models.UserCartViewModel
import com.example.seabuckcafe.utils.Constants
import java.text.NumberFormat

class UserProductListItemAdapter(
    private val activity: Fragment,
    private val context: Context,
    private var productListItem: LiveData<MutableList<ProductItem>>
) : RecyclerView.Adapter<UserProductListItemAdapter.ItemListViewHolder>() {

    private val cartViewModel: UserCartViewModel by activity.activityViewModels()

    inner class ItemListViewHolder(val view: ListUserCartItemBinding) : RecyclerView.ViewHolder(view.root) {
        val imageView: ImageView = view.cartItemImage
        val itemName: TextView = view.cartItemName
        val itemPrice: TextView = view.cartItemPrice
        val itemQuantity: TextView = view.cartItemQuantity
        private val quantityDecrease: ImageButton = view.quantityMinusButton
        private val quantityIncrease: ImageButton = view.quantityAddButton
        private val cancelCart: ImageButton = view.cartItemCancel

        init {
            quantityDecrease.setOnClickListener { decrease() }
            quantityIncrease.setOnClickListener { increase() }
            cancelCart.setOnClickListener { cancelCartItem() }
        }

        private fun cancelCartItem() {
            val item = productListItem.value!![adapterPosition]
            cartViewModel.product.value!!.removeAt(adapterPosition)
            cartViewModel.badgeQuantityDecrease(item.quantity)

            val subTotal = item.quantity.toInt() * item.price.toDouble()
            cartViewModel.minusSubTotal(subTotal)
            notifyItemRemoved(adapterPosition)
        }

        private fun increase() {
            val item = productListItem.value!![adapterPosition]
            item.quantity = item.quantity.toInt() + Constants.QUANTITY_INCREASE
            cartViewModel.productQuantityIncrease(adapterPosition, item)
            cartViewModel.badgeQuantityIncrease(Constants.QUANTITY_INCREASE)
            itemQuantity.text = item.quantity.toString()
        }

        private fun decrease() {
            val item = productListItem.value!![adapterPosition]

            if (item.quantity.toInt() != 1) {
                item.quantity = item.quantity.toInt() - Constants.QUANTITY_DECREASE
                cartViewModel.productQuantityDecrease(adapterPosition, item)
                cartViewModel.badgeQuantityDecrease(Constants.QUANTITY_DECREASE)
                itemQuantity.text = item.quantity.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val binding = ListUserCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val item = productListItem.value?.get(position)

        Glide.with(context)
            .load(item!!.image)
            .centerCrop()
            .into(holder.imageView)

        holder.itemName.text = item.itemName

        val priceFormat = NumberFormat.getInstance().format(item.price.toDouble())
        holder.itemPrice.text = context.getString(R.string.rm, priceFormat.toDouble())
        holder.itemQuantity.text = item.quantity.toString()
    }

    override fun getItemCount(): Int {
        return productListItem.value!!.size
    }
}


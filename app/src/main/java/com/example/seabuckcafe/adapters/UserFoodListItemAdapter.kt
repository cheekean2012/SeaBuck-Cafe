package com.example.seabuckcafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.ListFoodItemBinding
import com.example.seabuckcafe.models.MenuSharedViewModel
import com.example.seabuckcafe.models.UserMenuItem
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat

class UserFoodListItemAdapter(
    private val activity: Fragment,
    private val context: Context,
    private val foodItemList: MutableList<UserMenuItem>) : RecyclerView.Adapter<UserFoodListItemAdapter.FoodListViewHolder>() {

    private val shareViewModel: MenuSharedViewModel by activity.activityViewModels()

    inner class FoodListViewHolder(val view: ListFoodItemBinding) : RecyclerView.ViewHolder(view.root) {
        val imageView: ImageView = view.foodImageInUserAdminMenu
        val foodTitle: TextView = view.foodTitleInUserAdminMenu
        val foodPrice: TextView = view.foodPriceInUserAdminMenu
        private var card: MaterialCardView = view.card

        init {
            card.setOnClickListener{ goToFoodDetail() }
        }

        private fun goToFoodDetail() {
            val position = foodItemList[adapterPosition]

            shareViewModel.setId(position.id!!)
            shareViewModel.setImage(position.image.toUri())
            shareViewModel.setTitle(position.title)
            shareViewModel.setType(position.type)
            shareViewModel.setDescription(position.description)
            shareViewModel.setPrice(position.price)
            shareViewModel.setAvailable(position.available!!)

            Utils().forward(activity, R.id.action_foodItemListFragment_to_userFoodItemDetailFragment)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        val binding: ListFoodItemBinding = ListFoodItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return FoodListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {
        val item = foodItemList[position]
        // Load image from http to url
        Glide.with(context)
            .load(item.image)
            .into(holder.imageView)

        holder.foodTitle.text = item.title

        // Set price format
        val priceFormat = NumberFormat.getNumberInstance().format(item.price.toDouble())
        holder.foodPrice.text = context.getString(R.string.rm, priceFormat.toString().toDouble())
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }


}
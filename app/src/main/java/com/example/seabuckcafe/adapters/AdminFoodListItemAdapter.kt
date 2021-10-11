package com.example.seabuckcafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seabuckcafe.R
import com.example.seabuckcafe.models.AdminMenuItem
import java.text.NumberFormat

class AdminFoodListItemAdapter(
    private val context: Context,
    private val foodItemList: ArrayList<AdminMenuItem>): RecyclerView.Adapter<AdminFoodListItemAdapter.FoodListViewHolder>() {

    inner class FoodListViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.foodImageInAdminMenu)
        val foodTitle: TextView = view.findViewById(R.id.foodTitleInAdminMenu)
        val foodPrice: TextView = view.findViewById(R.id.foodPriceInAdminMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        val foodListItemLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_food_item, parent, false)
        return FoodListViewHolder(foodListItemLayout)
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
        holder.foodPrice.text = context.getString(R.string.rm, priceFormat)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }
}
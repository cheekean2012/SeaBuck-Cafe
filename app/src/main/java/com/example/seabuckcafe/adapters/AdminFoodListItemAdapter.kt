package com.example.seabuckcafe.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seabuckcafe.R
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.AdminMenuItem
import com.example.seabuckcafe.models.MenuSharedViewModel
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.card.MaterialCardView
import java.text.NumberFormat

class AdminFoodListItemAdapter(
    private val activity: Fragment,
    private val context: Context,
    private val foodItemList: MutableList<AdminMenuItem>): RecyclerView.Adapter<AdminFoodListItemAdapter.FoodListViewHolder>() {

    private val shareViewModel: MenuSharedViewModel by activity.activityViewModels()

    inner class FoodListViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.foodImageInUserAdminMenu)
        val foodTitle: TextView = view.findViewById(R.id.foodTitleInUserAdminMenu)
        val foodPrice: TextView = view.findViewById(R.id.foodPriceInUserAdminMenu)
        private var card: MaterialCardView = view.findViewById(R.id.card)

        init {
            card.setOnClickListener { popupMenus(it) }

        }

        private fun popupMenus(view: View) {

            // Get current adapter position when user clicked menu in recycler view layout
            val position = foodItemList[adapterPosition]

            // Create popup menu layout
            val popupMenus = PopupMenu(context.applicationContext, view)
            popupMenus.inflate(R.menu.edit_menu)

            // Set drawable icon if SDK greater than 30
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenus.setForceShowIcon(true)
            }

            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.editText -> {
                        shareViewModel.setId(position.id!!)
                        shareViewModel.setImage(position.image.toUri())
                        shareViewModel.setTitle(position.title)
                        shareViewModel.setType(position.type)
                        shareViewModel.setDescription(position.description)
                        shareViewModel.setPrice(position.price)
                        shareViewModel.setAvailable(position.available!!)

                        Utils().forward(activity, R.id.action_adminFoodItemListFragment_to_adminFoodDetailFragment)
                        true
                    }
                    R.id.deleteText -> {
                        // Remove data from specify position
                        foodItemList.removeAt(adapterPosition)
                        // After remove and display
                        notifyDataSetChanged()
                        Firestore().deleteFoodMenuItem(activity, position.id!!)
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
        }
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
        holder.foodPrice.text = context.getString(R.string.rm, priceFormat.toDouble())
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }


}
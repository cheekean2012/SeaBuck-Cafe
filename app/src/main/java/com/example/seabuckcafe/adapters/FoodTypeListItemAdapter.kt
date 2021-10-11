package com.example.seabuckcafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.databinding.ListFoodTypeItemBinding

class FoodTypeListItemAdapter(
    private val context: Context,
    private val foodTypeListItem: List<String>): RecyclerView.Adapter<FoodTypeListItemAdapter.ItemViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int, foodType: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

        class ItemViewHolder(val view: ListFoodTypeItemBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(view.root) {
            val foodType: TextView = view.foodTypeText

            init {
                itemView.setOnClickListener {
                    listener.onItemClick(adapterPosition, foodType.text.toString())
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding: ListFoodTypeItemBinding = ListFoodTypeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = foodTypeListItem[position]
        holder.foodType.text = item
    }

    override fun getItemCount(): Int {
        return foodTypeListItem.size
    }
}
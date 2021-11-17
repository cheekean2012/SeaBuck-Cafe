package com.example.seabuckcafe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.ListOrderItemBinding
import com.example.seabuckcafe.models.UserOrderList
import com.example.seabuckcafe.models.UserOrderViewModel
import com.example.seabuckcafe.ui.admin.AdminReportListFragment
import com.example.seabuckcafe.ui.order.*
import com.example.seabuckcafe.utils.Utils

class OrderListItemAdapter(
    private val activity: Fragment,
    private val context: Context,
    private var orderList: MutableList<UserOrderList>): RecyclerView.Adapter<OrderListItemAdapter.OrderListViewHolder>() {

    private val orderViewModel: UserOrderViewModel by activity.activityViewModels()

    inner class OrderListViewHolder(val view: ListOrderItemBinding): RecyclerView.ViewHolder(view.root) {
        val orderDate: TextView = view.orderDate
        val foodName: TextView = view.foodNameInOrder
        val status: TextView = view.status
        val button: Button = view.forwardBtn

        init {
            button.setOnClickListener { forward() }
        }

        private fun forward() {
            val position = orderList[adapterPosition]

            orderViewModel.apply {
                setID(position.id!!)
                setUserID(position.userID)
                setName(position.userName)
                setStatus(position.status)
                setAddress(position.address)
                setOrderProduct(position.foodItem)
                setPaymentType(position.paymentType)
                setPickupType(position.pickupType)
                setPhoneNumber(position.phoneNumber)
                setTotalPrice(position.totalPrice)
                setReason(position.reason)
            }
            when(activity) {
                is UserOrderListFragment -> {
                    Utils().forward(activity, R.id.action_userOrderListFragment_to_userOrderDetailFragment)
                }
                is AdminOrderListFragment -> {
                    Utils().forward(activity, R.id.action_adminOrderListFragment_to_adminOrderDetailFragment)
                }
                is AdminOrderPendingListFragment -> {
                    Utils().forward(activity, R.id.action_adminOrderListFragment_to_adminOrderDetailFragment)
                }
                is AdminOrderPrepareListFragment -> {
                    Utils().forward(activity, R.id.action_adminOrderListFragment_to_adminOrderDetailFragment)
                }
                is AdminOrderDeliveringListFragment -> {
                    Utils().forward(activity, R.id.action_adminOrderListFragment_to_adminOrderDetailFragment)
                }
                is AdminReportListFragment -> {
                    Utils().forward(activity, R.id.action_adminReportListFragment_to_adminReportDetailFragment)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        val binding: ListOrderItemBinding = ListOrderItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        val item = orderList[position]

        orderViewModel.setOrderProduct(item.foodItem)
        orderViewModel.setStatus(item.status)

        val foodItem = item.foodItem?.map { it.itemName }
        val date = Utils().getDateString(item.date!!.seconds, "dd MMM yyyy, HH:mm")

        holder.orderDate.text = date
        holder.foodName.text = foodItem.toString().removeSurrounding("[", "]")
        holder.status.text = orderViewModel.status.value.toString()
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

}
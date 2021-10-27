package com.example.seabuckcafe.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserOrderViewModel: ViewModel() {

    private val _orderProduct = MutableLiveData<MutableList<ProductItem>?>()
    val orderProduct: MutableLiveData<MutableList<ProductItem>?> get() = _orderProduct

    fun setOrderProduct(foodItem: MutableList<ProductItem>?) {
        _orderProduct.value = foodItem
    }
}
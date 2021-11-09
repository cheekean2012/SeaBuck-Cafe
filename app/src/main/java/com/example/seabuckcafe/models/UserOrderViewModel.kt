package com.example.seabuckcafe.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserOrderViewModel: ViewModel() {

    private val _orderProduct = MutableLiveData<MutableList<ProductItem>?>()
    val orderProduct: MutableLiveData<MutableList<ProductItem>?> get() = _orderProduct

    private val _id = MutableLiveData<String>()
    val id: LiveData<String> get() = _id

    private val _userID = MutableLiveData<String>()
    val userID: LiveData<String> get() = _userID

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private val _pickupType = MutableLiveData<String>()
    val pickupType: LiveData<String> get() = _pickupType

    private val _paymentType = MutableLiveData<String>()
    val paymentType: LiveData<String> get() = _paymentType

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _totalPrice = MutableLiveData<String>()
    val totalPrice: LiveData<String> get() = _totalPrice

    private val _reason = MutableLiveData<String>()
    val reason: LiveData<String> get() = _reason

    fun setOrderProduct(foodItem: MutableList<ProductItem>?) {
        _orderProduct.value = foodItem
    }

    fun setID(id: String) {
        _id.value = id
    }

    fun setUserID(id: String) {
        _userID.value = id
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun setStatus(status: String) {
        _status.value = status
    }

    fun setPickupType(type: String) {
        _pickupType.value = type
    }

    fun setPaymentType(type: String) {
        _paymentType.value = type
    }

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun setTotalPrice(price: String) {
        _totalPrice.value = price
    }

    fun setReason(reason: String) {
        _reason.value = reason
    }

}
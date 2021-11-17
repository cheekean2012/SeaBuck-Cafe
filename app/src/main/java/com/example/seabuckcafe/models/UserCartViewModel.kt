package com.example.seabuckcafe.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserCartViewModel: ViewModel() {

    private val _product = MutableLiveData<MutableList<ProductItem>>()
    val product: MutableLiveData<MutableList<ProductItem>> get() = _product

    private val _quantity = MutableLiveData(0)
    val quantity: LiveData<Int> get() = _quantity

    private val _isQuantityZero = MutableLiveData(false)
    val isQuantityZero: LiveData<Boolean> get() = _isQuantityZero

    private val _subTotal = MutableLiveData(0.00)
    val subTotal: LiveData<Double> get() = _subTotal

    private val _pickType = MutableLiveData<String?>()
    val pickType: LiveData<String?> get() = _pickType

    private val _paymentType = MutableLiveData<String?>()
    val paymentType: LiveData<String?> get() = _paymentType

    private var isAdd: Boolean = false

    init {
        _product.value = ArrayList()
    }

    fun setProduct(product: ProductItem) {

      if (_product.value!!.isEmpty()) {

          _product.value!!.add(product)

      } else {

      // retrieve mutable live data to mutable list
      val cartItemList: MutableList<ProductItem> = _product.value!!
      Log.d("retrieve data", "$cartItemList")

      for (cartItem in cartItemList) {

          if (cartItem.itemName == product.itemName) {
              val index = cartItemList.indexOf(cartItem)
              cartItem.quantity = cartItem.quantity.toInt() + 1
              _product.value!![index] = cartItem
              isAdd = true
          }
      }

          if (!isAdd) {
              _product.value!!.add(product)
          }

      }
        isAdd = false
    }

    fun addSubTotal(price: Double) {
        _subTotal.value = (_subTotal.value)?.plus(price)
    }

    fun minusSubTotal(price: Double) {
        _subTotal.value = (_subTotal.value)?.minus(price)
    }

    fun productQuantityIncrease(position: Int, item: ProductItem) {
        _product.value!![position] = item
        addSubTotal(item.price.toDouble())
    }

    fun productQuantityDecrease(position: Int, item: ProductItem) {
        _product.value!![position] = item
        minusSubTotal(item.price.toDouble())
    }

    fun badgeQuantityDecrease(quantity: Number) {
        _quantity.value = (_quantity.value)?.minus(quantity.toInt())
        isVisible()
    }

    fun badgeQuantityIncrease(quantity: Number) {
        _quantity.value = (_quantity.value)?.plus(quantity.toInt())
        isVisible()
    }

    fun setPickupType(type: String) {
        _pickType.value = type
    }

    fun setPaymentType(type: String) {
        _paymentType.value = type
    }

    private fun isVisible() {
        // Checked if quantity is not 0, then set to true
        _isQuantityZero.value = _quantity.value != 0
    }

    fun clearProduct() {
        _product.value!!.clear()
        _quantity.value = 0
        _isQuantityZero.value = false
        _paymentType.value = null
        _pickType.value = null
        _subTotal.value = 0.00
    }
}


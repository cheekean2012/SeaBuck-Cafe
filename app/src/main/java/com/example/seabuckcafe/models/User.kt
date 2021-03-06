package com.example.seabuckcafe.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class User (
    val id: String = "",
    val userName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val image: String = "",
    val isUser: Boolean = true)


data class UserMenuItem (
    @DocumentId var id: String? = null,
    var image: String = "",
    val title: String = "",
    val type: String = "",
    val price: String = "",
    val description: String = "",
    val available: Boolean? = null)


data class ProductItem (
    var id: String = "",
    var image: String = "",
    var itemName: String = "",
    var quantity: Int = 0,
    var price: String = "")


data class UserAddressList (
    @DocumentId var id: String? = null,
    var address: String = ""
)

data class UserOrderList(
    @DocumentId var id: String? = null,
    val userID: String = "",
    @ServerTimestamp var date: Timestamp? = null,
    val dayOfMonth: String = "",
    val month: String = "",
    val year: String = "",
    val time: String = "",
    val userName: String = "",
    val phoneNumber: String = "",
    val foodItem: MutableList<ProductItem>? = null,
    val pickupType: String = "",
    val paymentType: String = "",
    val totalPrice: String = "",
    var status: String = "",
    var reason: String = "",
    var address: String = "")
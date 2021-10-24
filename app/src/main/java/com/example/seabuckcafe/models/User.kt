package com.example.seabuckcafe.models

import com.google.firebase.firestore.DocumentId

data class User (
    val id: String = "",
    val userName: String = "",
    val email: String = "",
    val phoneNumber: Long = 0,
    val image: String = "",
    val password: String = "",
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
    var quantity: Number = 0,
    var price: String = "")

data class UserAddressData (
    @DocumentId var id: String? = null,
    var address: String = ""
)
package com.example.seabuckcafe.models

import com.google.firebase.firestore.DocumentId

data class Admin (
    val id: String = "",
    val userName: String = "",
    val about: String = "",
    val address: String = "",
    val email: String = "",
    val phoneNumber: Long = 0,
    val isUser: Boolean = false)

data class AdminMenuItem(
    @DocumentId var id: String? = null,
    var image: String = "",
    val title: String = "",
    val type: String = "",
    val price: String = "",
    val description: String = "",
    val available: Boolean? = null)
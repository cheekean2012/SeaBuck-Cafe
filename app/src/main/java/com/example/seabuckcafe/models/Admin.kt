package com.example.seabuckcafe.models

class Admin (
    val id: String = "",
    val userName: String = "",
    val about: String = "",
    val address: String = "",
    val email: String = "",
    val phoneNumber: Long = 0,
    val isUser: Boolean = false)

class AdminMenuItem (
    var image: String = "",
    val title: String = "",
    val type: String = "",
    val price: Number = 0,
    val description: String = "",
    val available: Boolean )
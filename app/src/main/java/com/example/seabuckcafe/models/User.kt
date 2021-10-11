package com.example.seabuckcafe.models

class User (
    val id: String = "",
    val userName: String = "",
    val email: String = "",
    val phoneNumber: Long = 0,
    val image: String = "",
    val password: String = "",
    val isUser: Boolean = true)


class UserMenuItem (
    var image: String = "",
    val title: String = "",
    val type: String = "",
    val price: Number = 0,
    val description: String = "",
    val available: Boolean )
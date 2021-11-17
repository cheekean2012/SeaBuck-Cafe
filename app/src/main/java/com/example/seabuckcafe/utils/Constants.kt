package com.example.seabuckcafe.utils

object Constants {
    const val USERS: String = "users"
    const val ADMIN: String = "admin"
    const val MENUS: String = "menus"
    const val ADDRESS: String = "addresses"
    const val USER_ADDRESS: String = "userAddress"
    const val ORDERS: String = "orders"
    const val STATUS_PENDING: String = "pending"
    const val STATUS_ON_PREPARE: String = "on prepare"
    const val STATUS_DELIVERING: String = "delivering"
    const val STATUS_COMPLETE: String = "complete"
    const val STATUS_CANCEL: String = "cancel"
    const val CHANNEL_ID: String = "order"
    const val QUANTITY_INCREASE: Int = 1
    const val QUANTITY_DECREASE: Int = 1

    fun foodType(): List<String>{
        return listOf(
            ("Breakfast"),
            ("Lunch"),
            ("Dinner"),
            ("Snacks"),
            ("Drinks"),
            ("Dessert"),
            ("Others"),
            ("Side Dishes")
        )
    }
}
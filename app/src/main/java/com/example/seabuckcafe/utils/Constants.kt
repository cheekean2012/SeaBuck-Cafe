package com.example.seabuckcafe.utils

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment

object Constants {
    const val USERS: String = "users"
    const val ADMIN: String = "admin"
    const val MENUS: String = "menus"
    const val ADDRESS: String = "addresses"
    const val FOOD_IMAGE: String = "food_image"
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

    fun getFileExtension(activity: Fragment, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.requireActivity().contentResolver.getType(uri!!))
    }

}
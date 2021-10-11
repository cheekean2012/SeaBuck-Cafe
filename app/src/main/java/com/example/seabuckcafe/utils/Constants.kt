package com.example.seabuckcafe.utils

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment

object Constants {
    const val USERS: String = "users"
    const val ADMIN: String = "admin"
    const val MENUS: String = "menus"
    const val FOOD_IMAGE: String = "food_image"

    fun foodType(): List<String>{
        return listOf(
            ("Breakfast"),
            ("Lunch"),
            ("Dinner"),
            ("Snacks"),
            ("Drinks"),
            ("Dessert"),
            ("Others")
        )
    }

    fun getFileExtension(activity: Fragment, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.requireActivity().contentResolver.getType(uri!!))
    }

}
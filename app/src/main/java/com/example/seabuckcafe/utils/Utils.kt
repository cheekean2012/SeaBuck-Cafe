package com.example.seabuckcafe.utils

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import java.text.SimpleDateFormat
import java.util.*

class Utils: Fragment() {

    lateinit var getContent : ActivityResultLauncher<String>
    var filterDate: String = ""
    var dateFormat: String = ""

    // Set to back previous page
    fun backward(fragment: Fragment, navItemId: Int) {
        findNavController(fragment).popBackStack(navItemId, false)
    }

    // Set to go next page
    fun forward(fragment: Fragment, navItemId: Int) {
        findNavController(fragment).navigate(navItemId)
    }

    fun getDateString(seconds: Long, outputPattern: String): String {
        return try {
            val dateFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = seconds * 1000
            val date = calendar.time
            dateFormat.format(date)
        } catch (e: Exception) {
            Log.e("utils", "Date format", e)
            ""
        }
    }

}
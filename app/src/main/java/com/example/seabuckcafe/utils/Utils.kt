package com.example.seabuckcafe.utils

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import java.text.SimpleDateFormat
import java.util.*

class Utils: Fragment() {

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

    fun handleKeyEvent(view: View, keyCode: Int, context: Context): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //hide the keyboard
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}

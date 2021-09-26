package com.example.seabuckcafe.utils

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController

class Utils: Fragment() {

    // Set to back previous page
    fun backward(fragment: Fragment, navItemId: Int) {
        findNavController(fragment).popBackStack(navItemId, false)
    }

    // Set to go next page
    fun forward(fragment: Fragment, navItemId: Int) {
        findNavController(fragment).navigate(navItemId)
    }
}
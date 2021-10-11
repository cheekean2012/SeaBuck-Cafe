package com.example.seabuckcafe.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminFoodDetailViewModel: ViewModel() {

    private val _imageURL = MutableLiveData<String>()
    val imageURL: LiveData<String> get() = _imageURL

    fun setImageURL(imageUrl: String) {
        _imageURL.value = imageUrl
    }

}
package com.example.seabuckcafe.models

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MenuSharedViewModel : ViewModel() {

    private val _id = MutableLiveData<String?>()
    val id: LiveData<String?> get() = _id

    private val _image = MutableLiveData<Uri?>()
    val image: LiveData<Uri?> get() = _image

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> get() = _title

    private val _type = MutableLiveData<String?>()
    val type: LiveData<String?> get() = _type

    private val _price = MutableLiveData<String?>()
    val price: LiveData<String?> get() = _price

    private val _description = MutableLiveData<String?>()
    val description: LiveData<String?> get() = _description

    private val _available = MutableLiveData<Boolean>()
    val available: LiveData<Boolean> get() = _available

    fun setId(id: String) {
        _id.value = id
    }

    fun setImage(image: Uri) {
        _image.value = image
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setType(type: String) {
        _type.value = type
    }

    fun setPrice(price: String) {
        _price.value = price
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setAvailable(available: Boolean) {
        _available.value = available
    }

    fun resetValue() {
        _image.value = null
        _type.value = null
        _title.value = null
        _description.value = null
        _available.value = true
        _price.value = null
    }
}
package com.example.seabuckcafe.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserAddressModel: ViewModel() {

    private val _address = MutableLiveData<UserAddressData>()
    val address: LiveData<UserAddressData> get() = _address

}
package com.example.seabuckcafe.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.seabuckcafe.databinding.FragmentRegisterBinding

class RegisterViewModel : ViewModel() {

    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> get() = _fullName

    private val _emailAddress = MutableLiveData<String>()
    val emailAddress: LiveData<String> get() = _emailAddress

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private lateinit var binding: FragmentRegisterBinding

    init {
        resetRegister()
    }

    fun setFullName(fullName: String) {
        _fullName.value = fullName
    }

    fun setEmailAddress(emailAddress: String) {
        _emailAddress.value = emailAddress
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    private fun resetRegister() {
        _fullName.value = ""
        _emailAddress.value = ""
        _phoneNumber.value = ""
        _password.value = ""
    }
}
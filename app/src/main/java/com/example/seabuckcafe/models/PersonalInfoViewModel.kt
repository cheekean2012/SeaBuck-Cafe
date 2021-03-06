package com.example.seabuckcafe.models

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonalInfoViewModel: ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _forwardText = MutableLiveData<String>()
    val forwardText: LiveData<String> get() = _forwardText

    private val _image = MutableLiveData<Uri?>()
    val image: LiveData<Uri?> get() = _image

    fun setName(name: String) {
        _name.value = name
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setForwardText(text: String) {
        _forwardText.value = text
    }

    fun setProfileImage(uri: Uri) {
        _image.value = uri
    }
}
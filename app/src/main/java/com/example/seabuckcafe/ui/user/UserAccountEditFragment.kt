package com.example.seabuckcafe.ui.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserEditAccountBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.PersonalInfoViewModel
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class UserAccountEditFragment: Fragment() {

    private lateinit var binding: FragmentUserEditAccountBinding
    private val personalInfoViewModel: PersonalInfoViewModel by activityViewModels()
    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForwardButton()

        binding.apply {

            topAppBar.setNavigationOnClickListener { backward() }

            fullNameEditText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
            currentPasswordText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
            newPasswordText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
            phoneNumberEditText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }

            save.setOnClickListener { save() }
        }
    }

    private fun checkForwardButton() {

        when (personalInfoViewModel.forwardText.value.toString()) {

            getString(R.string.your_fullName) -> {
                binding.fullNameField.visibility = View.VISIBLE
            }
            getString(R.string.your_password) -> {
                binding.currentPassword.visibility = View.VISIBLE
                binding.newPassword.visibility = View.VISIBLE
            }
            getString(R.string.your_phone_number) -> {
                binding.phoneNumberField.visibility = View.VISIBLE
            }
        }
    }

    private fun save() {

        when (personalInfoViewModel.forwardText.value.toString()) {

            getString(R.string.your_fullName) -> {
                saveFullName()
            }
            getString(R.string.your_password) -> {
                savePassword()
            }
            getString(R.string.your_phone_number) -> {
                savePhoneNumber()
            }
        }

    }

    private fun savePassword() {

        binding.apply {
            val currentPassword = currentPasswordText.text.toString()
            val newPassword = newPasswordText.text.toString()
            val passwordValidationCheck = passwordLength(newPassword)

            if (passwordValidationCheck) {
                val user = auth.currentUser

                if (user != null && user.email != null) {
                    val authCredential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                    Log.d("email check", user.email!!)

                    user.reauthenticate(authCredential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {

                                user.updatePassword(newPassword).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(requireContext(), "Your password changed successful", Toast.LENGTH_SHORT).show()
                                        backward()
                                    }
                                }.addOnFailureListener { e ->
                                    Log.e("check error", e.toString())
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Please enter correctly your current password", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun savePhoneNumber() {
//        val accountInfo = HashMap<String, Any>()
//        accountInfo["userName"] = personalInfoViewModel.name.value.toString()
//        accountInfo["phoneNumber"] = personalInfoViewModel.phoneNumber.value.toString()
//
//        Firestore().updateUserAccountInfo(this@UserAccountEditFragment, accountInfo)

        var phoneNumber = binding.phoneNumberEditText.text.toString()
        val validCheck = phoneNumberValid(phoneNumber)
        phoneNumber = phoneNumber.replace("\\s".toRegex(), "")

        if (validCheck) {
            personalInfoViewModel.setPhoneNumber(phoneNumber)
            Firestore().updateUserPhoneNumber(requireContext(), phoneNumber)
            backward()
        }
    }

    private fun phoneNumberValid(phoneNumber: String): Boolean {

        return when {
            phoneNumber.isEmpty() -> {
                binding.phoneNumberField.error = "Please enter your phone number"
                false
            }
            phoneNumber.length < 9 -> {
                binding.phoneNumberField.error = "Your phone number must be at least 9 numbers"
                false
            }
            else -> true
        }
    }

    private fun saveFullName() {
        val name = binding.fullNameEditText.text.toString().trim{ it <= ' ' }
        val validCheck = userNameValid(name)

        if (validCheck) {
            personalInfoViewModel.setName(name)
            Firestore().updateUserName(requireContext(), name)
            backward()
        }
    }

    private fun userNameValid(name: String): Boolean {

        return when {
            name.isEmpty() -> {
                binding.fullNameField.error = "Please enter your full name"
                false
            }
            else -> true
        }
    }

    private fun passwordLength(password: String): Boolean {

        return when {
            password.length < 6 -> {
                binding.newPassword.error = "Please enter the password at least 6 digits"
                false
            }
            password.isEmpty() -> {
                binding.newPassword.error = "Please enter your password"
                false
            }
            else -> true
        }
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //hide the keyboard
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }


    private fun backward() {
        Utils().backward(this, R.id.userAccountFragment)
    }
}
package com.example.seabuckcafe.ui.user

import android.app.Dialog
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
    private lateinit var mProgressDialog: Dialog
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
            viewModel = personalInfoViewModel

            fullNameEditText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
            currentPasswordText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
            newPasswordText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
            phoneNumberEditText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }

            save.setOnClickListener { save() }
        }
    }

    private fun checkForwardButton() {

        binding.apply {
            when (personalInfoViewModel.forwardText.value.toString()) {

                getString(R.string.your_fullName) -> {
                    fullNameField.visibility = View.VISIBLE
                }
                getString(R.string.your_password) -> {
                    currentPassword.visibility = View.VISIBLE
                    newPassword.visibility = View.VISIBLE
                }
                getString(R.string.your_phone_number) -> {
                    phoneNumberField.visibility = View.VISIBLE
                }
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
            val passwordValidationCheck = validationCheck()

            if (passwordValidationCheck) {
                val user = auth.currentUser

                if (user != null && user.email != null) {

                    showProgress()
                    val authCredential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                    user.reauthenticate(authCredential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {

                                user.updatePassword(newPassword).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        closeProgress()
                                        Toast.makeText(requireContext(), "Your password changed successful", Toast.LENGTH_SHORT).show()
                                        backward()
                                    }
                                }.addOnFailureListener { e ->
                                    closeProgress()
                                    Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT).show()
                                    Log.e("check error", e.toString())
                                }
                            }
                        }.addOnFailureListener {
                            closeProgress()
                            Toast.makeText(requireContext(), "Please enter correctly your current password", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun savePhoneNumber() {

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

    private fun validationCheck(): Boolean {

        binding.apply {
            val current = currentPasswordText.text.toString()
            val new = newPasswordText.text.toString()

            if (current.isEmpty()) {
                currentPassword.error = getString(R.string.empty_blank)
                return false
            }

            if (current.length < 6) {
                currentPassword.error = getString(R.string.minimum_six_length)
                return false
            }

            currentPassword.error = null
            currentPassword.clearFocus()

            if (new.isEmpty()) {
                newPassword.error = getString(R.string.empty_blank)
                return false
            }

            if (new.length < 6) {
                newPassword.error = getString(R.string.minimum_six_length)
                return false
            }

            newPassword.error = null
            newPassword.clearFocus()

        }
        return true
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

    private fun showProgress() {
        mProgressDialog = Dialog(requireContext())

        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.show()
    }

    private fun closeProgress() {
        mProgressDialog.dismiss()
    }

    private fun backward() {
        Utils().backward(this, R.id.userAccountFragment)
    }
}
package com.example.seabuckcafe.ui.register

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentRegisterBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.User
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var mProgressDialog: Dialog
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerFragment = this@RegisterFragment

        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.passwordEditText.setOnKeyListener { view, keyCode, _ -> Utils().handleKeyEvent(view, keyCode, requireContext())  }
    }

    private fun backward() {
        Utils().backward(this, R.id.loginFragment)
    }

    fun signUpUser() {

        val validation = validCheck()

        if (validation) {
            val name = binding.fullNameEditText.text.toString().trim{ it <= ' ' }
            val email = binding.emailAddressEditText.text.toString().trim{ it <= ' ' }
            val password = binding.passwordEditText.text.toString().trim { it <= ' ' }
            val phoneNumber = binding.phoneNumberEditText.text.toString().trim { it <= ' ' }

            showProgress()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")

                        val firebaseUser = task.result!!.user!!
                        val userInfo = User(
                            firebaseUser.uid,
                            name,
                            email,
                            phoneNumber,
                            isUser = true)

                        Firestore().registerUser(this, userInfo)

                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        closeProgress()
                        Toast.makeText(activity, "The email address is already in use by another account.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Please fill correct your personal info", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validCheck(): Boolean {

        binding.apply {

            val name = fullNameEditText.text.toString()
            val email = emailAddressEditText.text.toString()
            val password = passwordEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()

            if (name.isEmpty()) {
                fullNameField.error = getString(R.string.empty_blank)
                return false
            }

            fullNameField.error = null
            fullNameField.clearFocus()

            if (email.isEmpty()) {
                emailAddressField.error = getString(R.string.empty_blank)
                return false
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailAddressField.error = getString(R.string.inValid_email)
                return false
            }

            emailAddressField.error = null
            emailAddressField.clearFocus()

            if (phoneNumber.isEmpty()) {
                phoneNumberField.error = getString(R.string.empty_blank)
                return false
            }

            phoneNumberField.error = null
            phoneNumberField.clearFocus()

            if (password.isEmpty()) {
                passwordField.error = getString(R.string.empty_blank)
                return false
            }

            if (password.length < 6) {
                passwordField.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                passwordEditText.text = null
                passwordField.error = getString(R.string.minimum_six_length)
                return false
            }
            passwordField.error = null
            passwordField.clearFocus()
        }
        return true
    }

    fun userSignUpSuccess() {
        val user: FirebaseUser? = auth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    closeProgress()
                    Toast.makeText(requireContext(), "An email has been sent. Please verify your account.", Toast.LENGTH_LONG).show()
                    Utils().backward(this, R.id.loginFragment)
                    Log.d(TAG, "Email sent. Please verify your account!")
                }
            }
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
}
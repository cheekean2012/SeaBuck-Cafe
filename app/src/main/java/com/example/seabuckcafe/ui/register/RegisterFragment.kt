package com.example.seabuckcafe.ui.register

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
    }

    private fun backward() {
        Utils().backward(this, R.id.loginFragment)
    }

    fun signUpUser() {

        if (binding.fullNameEditText.text.toString().isEmpty()) {
            binding.fullNameField.error = getString(R.string.empty_blank)
            return
        } else { binding.fullNameField.isErrorEnabled = false }

        if (binding.emailAddressEditText.text.toString().isEmpty()) {
            binding.emailAddressField.error = getString(R.string.empty_blank)
            return
        } else { binding.emailAddressField.isErrorEnabled = false }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressEditText.text.toString()).matches()) {
            binding.emailAddressField.error = getString(R.string.inValid_email)
            return
        } else { binding.emailAddressField.isErrorEnabled = false }

        if (binding.phoneNumberEditText.text.toString().isEmpty()) {
            binding.phoneNumberField.error = getString(R.string.empty_blank)
            return
        } else { binding.phoneNumberField.isErrorEnabled = false }

        if (binding.passwordEditText.text.toString().isEmpty()) {
            binding.passwordField.error = getString(R.string.empty_blank)
            return
        } else { binding.passwordField.isErrorEnabled = false }

        if (binding.passwordEditText.text.toString().length < 6) {
            binding.passwordField.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            binding.passwordEditText.text = null
            binding.passwordField.error = getString(R.string.minimum_six_length)
            return
        } else { binding.passwordField.isErrorEnabled = false }

        val name = binding.fullNameEditText.text.toString()
        val email = binding.emailAddressEditText.text.toString().trim{ it <= ' ' }
        val password = binding.passwordEditText.text.toString().trim { it <= ' ' }

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
                            binding.phoneNumberEditText.text.toString().toLong(),
                            isUser = true)

                    Firestore().registerUser(this, userInfo)

                } else {
                    // If sign up fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(activity, "The email address is already in use by another account.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun userSignUpSuccess() {
        val user: FirebaseUser? = auth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Utils().backward(this, R.id.loginFragment)
                    Log.d(TAG, "Email sent. Please verify your account!")
                }
            }
    }
}
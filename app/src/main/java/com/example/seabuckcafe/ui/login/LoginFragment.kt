package com.example.seabuckcafe.ui.login

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentLoginBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.Admin
import com.example.seabuckcafe.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding data
        binding.loginFragment = this@LoginFragment

        //create span text
        spannable()

    }

    override fun onStart() {
        super.onStart()
        // Initialize authentication
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                Firestore().getUserDetails(this)
            }
        }
    }

    private fun spannable(){
        val spannable = SpannableString(getString(R.string.action_sign_up))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                goToRegister()
            }
        }
        // Set text available clicked
        spannable.setSpan(
            clickableSpan,
            23, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set text underline with spannable
        spannable.setSpan(
            UnderlineSpan(),
            23, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set text color with spannable
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            23, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.registerText.setText(spannable, TextView.BufferType.SPANNABLE)
        binding.registerText.movementMethod = LinkMovementMethod.getInstance()
    }

    // Navigate to register
    fun goToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun goToHomeScreen() {

        // check validation before go to next fragment
        if (binding.emailAddressEditText.text.toString().isEmpty()) {
            binding.emailAddressField.error = getString(R.string.empty_blank)
            return
        } else { binding.emailAddressField.isErrorEnabled = false }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressEditText.text.toString()).matches()) {
            binding.emailAddressField.error = getString(R.string.inValid_email)
            return
        } else { binding.emailAddressField.isErrorEnabled = false }

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

        val email = binding.emailAddressEditText.text.toString().trim{ it <= ' ' }
        val password = binding.passwordEditText.text.toString().trim { it <= ' ' }

        // Sign in using authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")

                    val user:FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                    // If sign in fails, display a message to the user.
                    Toast.makeText(activity, "Invalid email or password!",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    // Check if the user has been login or isEmailVerified
    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {
            // Check if email has been verified navigate to user home screen
            if (currentUser.isEmailVerified) {
                // Get user details information
                Firestore().getUserDetails(this)
            } else {
                Toast.makeText(activity, "Please verify your email !",
                Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun userLoginSuccess(user: User){

        Log.d("Full Name", user.userName)
        Log.d("Email Address", user.email)
        Log.d("Phone Number", user.phoneNumber.toString())
        Log.d("isUser ", user.isUser.toString())

        Toast.makeText(activity, "Login Successful!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_loginFragment_to_homeUserFragment)
    }

    fun adminLoginSuccess(admin: Admin) {

        Log.d("Full Name", admin.userName)
        Log.d("Email Address", admin.email)
        Log.d("Phone Number", admin.phoneNumber.toString())
        Log.d("isUser ", admin.isUser.toString())

        Toast.makeText(activity, "Login Successful!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_loginFragment_to_homeAdminFragment)
    }

}
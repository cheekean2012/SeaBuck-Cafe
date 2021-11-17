package com.example.seabuckcafe.ui.login

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentLoginBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.Admin
import com.example.seabuckcafe.models.PersonalInfoViewModel
import com.example.seabuckcafe.models.User
import com.example.seabuckcafe.utils.Utils
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val personalInfoViewModel: PersonalInfoViewModel by activityViewModels()
    private lateinit var mProgressDialog: Dialog
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            )
        }

        binding.loginFragment = this@LoginFragment
        binding.passwordEditText.setOnKeyListener{view, keyCode, _ -> Utils().handleKeyEvent(view, keyCode, requireContext())}

        //create span text
        spannable()

    }

    override fun onStart() {
        super.onStart()
        // Initialize authentication
        auth = FirebaseAuth.getInstance()
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

    fun goToResetPassword() {
        findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    fun goToHomeScreen() {

        val checkValidation = validInfo()

        if (checkValidation) {
            val email = binding.emailAddressEditText.text.toString().trim{ it <= ' ' }
            val password = binding.passwordEditText.text.toString().trim { it <= ' ' }

            showProgress()

            // Sign in using authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "signInWithEmail:success")

                        val user:FirebaseUser? = auth.currentUser
                        updateUI(user)
                    } else {
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)

                        closeProgress()

                        // If sign in fails, display a message to the user.
                        Toast.makeText(activity, "Invalid email or password!",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun validInfo(): Boolean {

        binding.apply {
            val email = emailAddressEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty()) {
                emailAddressField.error = getString(R.string.empty_blank)
                return false
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailAddressField.error = getString(R.string.inValid_email)
                return false
            }

            emailAddressField.error= null
            emailAddressField.clearFocus()

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

    // Check if the user has been login or isEmailVerified
    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {
            // Check if email has been verified navigate to user home screen
            if (currentUser.isEmailVerified) {
                // Get user details information
                Firestore().getUserDetails(this)
            } else {
                closeProgress()
                Toast.makeText(activity, "Please verify your email !",
                Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun userLoginSuccess(user: User){

        closeProgress()
        Log.d("Full Name", user.userName)
        Log.d("Email Address", user.email)
        Log.d("Phone Number", user.phoneNumber)
        Log.d("isUser ", user.isUser.toString())

        personalInfoViewModel.setName(user.userName)
        personalInfoViewModel.setEmail(user.email)
        personalInfoViewModel.setPhoneNumber(user.phoneNumber)
        personalInfoViewModel.setProfileImage(user.image.toUri())

        findNavController().navigate(R.id.action_loginFragment_to_homeUserFragment)
    }

    fun adminLoginSuccess(admin: Admin) {

        closeProgress()
        Log.d("Full Name", admin.userName)
        Log.d("Email Address", admin.email)
        Log.d("Phone Number", admin.phoneNumber)
        Log.d("isUser ", admin.isUser.toString())

        personalInfoViewModel.setName(admin.userName)
        personalInfoViewModel.setEmail(admin.email)
        personalInfoViewModel.setPhoneNumber(admin.phoneNumber)

        findNavController().navigate(R.id.action_loginFragment_to_homeAdminFragment)
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
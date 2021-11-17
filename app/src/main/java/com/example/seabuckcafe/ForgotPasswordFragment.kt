package com.example.seabuckcafe

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.databinding.FragmentForgotPasswordBinding
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment: Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var mProgressDialog: Dialog
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding.topAppBar.setNavigationOnClickListener { backward() }
        binding.emailAddressEditText.setOnKeyListener { view, keyCode, _ -> Utils().handleKeyEvent(view, keyCode, requireContext()) }
        binding.sendButton.setOnClickListener { resetPassword() }
    }

    private fun resetPassword() {

        val email = binding.emailAddressEditText.text.toString().trim { it <= ' ' }

        val validation = emailCheck(email)

        if (validation) {
            showProgress()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        closeProgress()
                        Toast.makeText(requireContext(), "Reset password has been sent", Toast.LENGTH_SHORT).show()
                        backward()
                    }
                }.addOnFailureListener {
                    closeProgress()
                    Toast.makeText(requireContext(), "Invalid email", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun emailCheck(email: String): Boolean {

        binding.apply {

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
            return true
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

    private fun backward() {
        Utils().backward(this, R.id.loginFragment)
    }
}
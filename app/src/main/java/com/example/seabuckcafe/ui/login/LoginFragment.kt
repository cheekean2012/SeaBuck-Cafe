package com.example.seabuckcafe.ui.login

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null

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
        binding?.loginFragment = this@LoginFragment

        //hide action bar when starting to create login fragment
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        spannable()
    }

    private fun spannable(){
        val spannable = SpannableString(getString(R.string.action_sign_up))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                goToRegister()
            }
        }

        spannable.setSpan(
            clickableSpan,
            23, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //set text underline with spannable
        spannable.setSpan(
            UnderlineSpan(),
            23, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //set text color with spannable
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            23, 31,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding?.registerText?.setText(spannable, TextView.BufferType.SPANNABLE)
        binding?.registerText?.movementMethod = LinkMovementMethod.getInstance()
    }

    fun goToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        binding = null
    }
}
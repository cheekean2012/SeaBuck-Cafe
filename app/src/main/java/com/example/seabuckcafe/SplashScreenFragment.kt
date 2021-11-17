package com.example.seabuckcafe

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.seabuckcafe.databinding.FragmentSplashScreenBinding
import com.example.seabuckcafe.firestore.Firestore
import com.example.seabuckcafe.models.Admin
import com.example.seabuckcafe.models.PersonalInfoViewModel
import com.example.seabuckcafe.models.User
import com.example.seabuckcafe.utils.Utils
import com.google.firebase.auth.FirebaseAuth

class SplashScreenFragment: Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding
    private val personalInfoViewModel: PersonalInfoViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }

        // Initialize authentication
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                Firestore().getUserDetails(this)
            } else {
                Utils().forward(this, R.id.action_splashScreenFragment_to_loginFragment)
            }
        } else {
            Utils().forward(this, R.id.action_splashScreenFragment_to_loginFragment)
        }
    }

    fun userLoginSuccess(user: User){

        Log.d("Full Name", user.userName)
        Log.d("Email Address", user.email)
        Log.d("Phone Number", user.phoneNumber)
        Log.d("isUser ", user.isUser.toString())

        personalInfoViewModel.setName(user.userName)
        personalInfoViewModel.setEmail(user.email)
        personalInfoViewModel.setPhoneNumber(user.phoneNumber)
        personalInfoViewModel.setProfileImage(user.image.toUri())

        findNavController().navigate(R.id.action_splashScreenFragment_to_homeUserFragment)
    }

    fun adminLoginSuccess(admin: Admin) {

        Log.d("Full Name", admin.userName)
        Log.d("Email Address", admin.email)
        Log.d("Phone Number", admin.phoneNumber)
        Log.d("isUser ", admin.isUser.toString())

        personalInfoViewModel.setName(admin.userName)
        personalInfoViewModel.setEmail(admin.email)
        personalInfoViewModel.setPhoneNumber(admin.phoneNumber)

        findNavController().navigate(R.id.action_splashScreenFragment_to_homeAdminFragment)
    }
}
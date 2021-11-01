package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserAccountBinding
import com.example.seabuckcafe.models.PersonalInfoViewModel
import com.example.seabuckcafe.utils.Utils


class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding
    private val personalInfoViewModel: PersonalInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            viewModel = personalInfoViewModel

            userAccountBinding = this@UserAccountFragment

            topAppBar.setNavigationOnClickListener { backward() }
        }
    }

    private fun forward() {
        Utils().forward(this, R.id.action_userAccountFragment_to_userAccountEditFragment)
    }

    fun setForwardText(text: String) {
        personalInfoViewModel.setForwardText(text)
        forward()
    }

    fun backward() {
        Utils().backward(this, R.id.userProfileFragment)
    }

}
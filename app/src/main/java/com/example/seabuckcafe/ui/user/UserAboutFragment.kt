package com.example.seabuckcafe.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.seabuckcafe.R
import com.example.seabuckcafe.databinding.FragmentUserAboutBinding
import com.example.seabuckcafe.utils.Utils

class UserAboutFragment: Fragment() {
    private lateinit var binding: FragmentUserAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutDescription.text = "Established in 2010, SeaBuck Cafe is a locally-owned restaurant that aims to provide top quality service to all its customers." +
                " independent catering and restaurant service." +
                " We work together to create a distinct brand among the many choices in the cafe industry." +
                " We’re still happily serving the customers with fresh quality food.\n\n" +
                " We serve our customers with aromatic coffee, delicious food and high quality customer service. " +
                " Our service has become one of the most popular and most young people like to enjoy." +
                " We pursue our craft of food and coffee to build a community gathering space." +
                " It’s almost as popular as our famous main dishes, dessert, etc!"

        binding.topAppBar.setNavigationOnClickListener { backward() }
    }

    private fun backward() {
        Utils().backward(this, R.id.homeUserFragment)
    }
}
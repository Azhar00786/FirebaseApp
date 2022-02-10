package com.example.firebaseapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.firebaseapp.R
import com.example.firebaseapp.repository.FirebaseRepo
import com.example.firebaseapp.viewmodels.HomeFragVM
import com.example.firebaseapp.viewmodels.HomeFragVMFactory
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeFragVM
    private lateinit var viewModelFactory: HomeFragVMFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelCall()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutButton.setOnClickListener {
            viewModel.clearAll()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
    }

    private fun viewModelCall() {
        val firebaseRepo: FirebaseRepo = FirebaseRepo(requireContext())
        viewModelFactory = HomeFragVMFactory(firebaseRepo)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeFragVM::class.java)
    }
}
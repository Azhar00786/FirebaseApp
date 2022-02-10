package com.example.firebaseapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.firebaseapp.R
import com.example.firebaseapp.repository.FirebaseRepo
import com.example.firebaseapp.utils.CustomProgressView
import com.example.firebaseapp.utils.LoginFailedState
import com.example.firebaseapp.utils.NetworkChecker
import com.example.firebaseapp.viewmodels.LoginFragmentVM
import com.example.firebaseapp.viewmodels.LoginFragmentVMFactory
import com.example.firebaseapp.viewmodels.SharedVM
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginFragmentVM
    private lateinit var viewModelFactory: LoginFragmentVMFactory
    private val sharedViewModel: SharedVM by activityViewModels()
    private var progressView: CustomProgressView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelCall()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressView = CustomProgressView(requireContext())

        otpButton.setOnClickListener {
            if (validatePhoneNumber(phoneNumber.text.toString())) {
                viewModel.clearAllSignedData()
                validate()
                //subscribeObservers()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid mobile number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validate() {
        try {
            val phoneNumber = phoneNumber.text
            val fullPhoneNumber = "+91$phoneNumber"
            Log.d("phoneNumber", fullPhoneNumber)
            if (NetworkChecker.isNoInternet(requireContext())) {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
            } else {
                viewModel.otpRequester(fullPhoneNumber, requireActivity())
                subscribeObservers()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun subscribeObservers() {
        try {
            viewModel.getProgress().observe(viewLifecycleOwner) {
                if (it) {
                    progressView?.show()
                } else {
                    progressView?.dismiss()
                }
            }

            viewModel.getFailed().observe(viewLifecycleOwner) {
                if (it != LoginFailedState.NoError) {
                    viewModel.setProgress(false)
                    Toast.makeText(requireContext(), "[ERROR]$it", Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.getVerificationId().observe(viewLifecycleOwner) {
                it.let {
                    //viewModel.setVCodeNull()
                    if (it != null) {
                        viewModel.setProgress(false)
                        sharedViewModel.setVCode(it)
                        findNavController().navigate(R.id.action_loginFragment_to_otpProcessingFragment)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun viewModelCall() {
        val firebaseRepo: FirebaseRepo = FirebaseRepo(requireContext())
        viewModelFactory = LoginFragmentVMFactory(firebaseRepo)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginFragmentVM::class.java)
    }

    private fun validatePhoneNumber(number: String): Boolean {
        return number.length == 10 && number.isNotEmpty() && number.isNotBlank()
    }
}
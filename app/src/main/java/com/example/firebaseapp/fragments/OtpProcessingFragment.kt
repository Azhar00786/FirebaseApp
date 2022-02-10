package com.example.firebaseapp.fragments

import android.os.Bundle
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
import com.example.firebaseapp.viewmodels.OtpProcessingFragmentVM
import com.example.firebaseapp.viewmodels.OtpProcessingFragmentVMFactory
import com.example.firebaseapp.viewmodels.SharedVM
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_otp_processing.*

class OtpProcessingFragment : Fragment() {
    private var progressView: CustomProgressView? = null
    private lateinit var viewModel: OtpProcessingFragmentVM
    private lateinit var viewModelFactory: OtpProcessingFragmentVMFactory
    private val sharedVM: SharedVM by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelCall()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressView = CustomProgressView(requireContext())
        otpSubmitButton.setOnClickListener {
            if (validateOtp(otpNumber.text.toString())) {
                authenticateOtp(otpNumber.text.toString())
            } else {
                Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subscribeObservers() {
        try {
            viewModel.getVProgress().observe(viewLifecycleOwner) {
                if (it) {
                    progressView?.show()
                } else {
                    progressView?.dismiss()
                }
            }

            viewModel.getFailed().observe(viewLifecycleOwner) {
                if (it != LoginFailedState.NoError) {
                    viewModel.setVProgress(false)
                    Toast.makeText(requireContext(), "[ERROR]$it", Toast.LENGTH_SHORT).show()
                }
            }

            viewModel.getTaskResult().observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.setVProgress(false)
                    findNavController().navigate(R.id.action_otpProcessingFragment_to_homeFragment)
                } else {
                    viewModel.setVProgress(false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateOtp(otp: String): Boolean {
        return otp.isNotEmpty() && otp.isNotBlank()
    }

    private fun authenticateOtp(otpNumber: String) {
        try {
            when {
                NetworkChecker.isNoInternet(requireContext()) -> {
                    Toast.makeText(requireContext(), "No Internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    signInWithCredential(otpNumber)
                    subscribeObservers()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun signInWithCredential(otpNumber: String) {
        try {
            val verificationCode = sharedVM.getVCode()
            val credential = PhoneAuthProvider.getCredential(verificationCode, otpNumber)
            viewModel.setCredential(credential)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun viewModelCall() {
        viewModelFactory = OtpProcessingFragmentVMFactory(FirebaseRepo(requireContext()))
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(OtpProcessingFragmentVM::class.java)
    }
}
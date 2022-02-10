package com.example.firebaseapp.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import android.app.Activity
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.firebaseapp.utils.LoginFailedState
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class FirebaseRepo(private val context: Context) {
    private val taskResult: MutableLiveData<Boolean> = MutableLiveData()
    private var verificationLD: MutableLiveData<String> = MutableLiveData()
    private val failedState: MutableLiveData<LoginFailedState> = MutableLiveData()
    val credential: MutableLiveData<PhoneAuthCredential> = MutableLiveData()

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getCurrentLoggedInUser(): FirebaseUser? = firebaseAuth.currentUser

    fun getPhoneAuthCredential(verificationId: String, code: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, code)
    }

    fun setCredential(credential: PhoneAuthCredential) {
        signInWithPhoneAuthCredential(credential)
    }

    fun getCredential(): LiveData<PhoneAuthCredential> {
        return credential
    }

    fun createCredential(otpNumber: String): PhoneAuthCredential {
        Log.d("FirebaseRepo", verificationLD.value!!)
        return PhoneAuthProvider.getCredential(verificationLD.value!!, otpNumber)
    }

    fun getTaskResult(): LiveData<Boolean> {
        return taskResult
    }

    fun getFailed(): LiveData<LoginFailedState> {
        return failedState
    }

    fun getVerificationCode(): MutableLiveData<String> {
        return verificationLD
    }

    fun getVerificationIdStr(): String {
        return verificationLD.value!!
    }

    fun setVerificationCodeNull() {
        verificationLD.value = null
    }

    fun clearOldAuth() {
        credential.value = null
        taskResult.value = null
    }

    fun clearAllSignInData() {
        credential.value = null
        taskResult.value = null
        verificationLD.value = null
        failedState.value = LoginFailedState.NoError
    }

    fun signOutAcct() = firebaseAuth.signOut()

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignIn", "signInWithCredential:success")
                    taskResult.value = true
                    Log.d("SignIn", taskResult.value.toString())
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("SignIn", "signInWithCredential:fail:", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                    // The verification code entered was invalid
                    //"VerificationCodeWrong"
                        Log.d("SignIn", "Invalid verification code")
                    failedState.value = LoginFailedState.Signin

                    // Update UI
                }
            }
    }

    fun otpGenerator(phoneNumber: String, requireActivity: Activity) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            //Instant verification is kept off, so users can login by entering OTP only
            Log.d("callbacks", "onVerificationCompleted:$credential")
            /*
            this@FirebaseRepo.credential.value = credential
            Handler().postDelayed({
                signInWithPhoneAuthCredential(credential)
            }, 4000)
            */
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("callbacks ERROR", "onVerificationFailed", e)
            failedState.value = LoginFailedState.Verification
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("callback ERROR", "InvalidCreditiantial")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("callback ERROR", "SMS Quota finished")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, token)
            Log.d("callbacks", "onCodeSent VID:$verificationId")
            this@FirebaseRepo.verificationLD.value = verificationId
        }
    }
}
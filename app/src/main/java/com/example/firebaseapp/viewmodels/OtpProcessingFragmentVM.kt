package com.example.firebaseapp.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebaseapp.repository.FirebaseRepo
import com.example.firebaseapp.utils.LoginFailedState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential

class OtpProcessingFragmentVM(private val repo: FirebaseRepo) : ViewModel() {
    private val vProgress = MutableLiveData(false)
    var verifyCode: String = ""

    private fun getCurrentLoggedInUser(): FirebaseUser? {
        return if (repo.getCurrentLoggedInUser() == null) {
            null
        } else {
            repo.getCurrentLoggedInUser()
        }
    }

    fun logOutUser() = repo.signOutAcct()

    fun getAuthCredentials(verificationId: String, code: String): PhoneAuthCredential {
        return repo.getPhoneAuthCredential(verificationId, code)
    }

    fun otpRequester(phoneNumber: String, requireActivity: Activity) {
        repo.otpGenerator(phoneNumber, requireActivity)
    }

    fun getTaskResult(): LiveData<Boolean> {
        return repo.getTaskResult()
    }

    fun setVProgress(show: Boolean) {
        vProgress.value = show
    }

    fun getCredential(): LiveData<PhoneAuthCredential> {
        return repo.getCredential()
    }

    fun setCredential(credential: PhoneAuthCredential) {
        setVProgress(true)
        repo.setCredential(credential)
    }

    fun createCredential(otpNumber: String): PhoneAuthCredential {
        return repo.createCredential(otpNumber)
    }

    fun getVProgress(): LiveData<Boolean> {
        return vProgress
    }

    fun getVerificationCodeLD(): MutableLiveData<String> {
        return repo.getVerificationCode()
    }

    fun getVerificationCode(): String {
        verifyCode = repo.getVerificationIdStr()
        return verifyCode
    }

    fun setVCodeNull() {
        //val verifyCode=repo.getVerificationCode().value!!
        repo.setVerificationCodeNull()
        //return verifyCode
    }

    fun getFailed(): LiveData<LoginFailedState> {
        return repo.getFailed()
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) =
        repo.signInWithPhoneAuthCredential(credential)

    fun clearAll() {
        //userProfileGot.value=null
        if (getCurrentLoggedInUser() != null)
            repo.signOutAcct()
        repo.clearOldAuth()
    }

    fun clearOldAuthData() {
        repo.clearOldAuth()
    }
}

class OtpProcessingFragmentVMFactory(private val repo: FirebaseRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtpProcessingFragmentVM::class.java)) {
            return OtpProcessingFragmentVM(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
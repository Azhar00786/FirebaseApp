package com.example.firebaseapp.viewmodels

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebaseapp.repository.FirebaseRepo
import com.example.firebaseapp.utils.LoginFailedState
import com.google.firebase.auth.FirebaseUser

class LoginFragmentVM(private val repo: FirebaseRepo) : ViewModel() {
    private val progress = MutableLiveData(false)

    fun getCurrentLogInUser(): FirebaseUser? {
        return if (repo.getCurrentLoggedInUser() == null) {
            null
        } else {
            repo.getCurrentLoggedInUser()
        }
    }

    fun logOutAcc() = repo.signOutAcct()

    fun otpRequester(phoneNumber: String, requireActivity: Activity) {
        setProgress(true)
        repo.otpGenerator(phoneNumber, requireActivity)
    }

    fun setProgress(show: Boolean) {
        progress.value = show
    }

    fun setVCodeNull() {
        repo.setVerificationCodeNull()
    }

    fun clearAllSignedData() {
        if (repo.getCurrentLoggedInUser() != null)
            repo.signOutAcct()
        repo.clearAllSignInData()
    }

    fun clearAuthData() {
        //userProfileGot.value=null
        if (repo.getCurrentLoggedInUser() != null)
            repo.signOutAcct()
        repo.clearOldAuth()
    }

    fun getVerificationId(): MutableLiveData<String> {
        return repo.getVerificationCode()
    }

    fun getProgress(): LiveData<Boolean> {
        return progress
    }

    fun getTaskResult(): LiveData<Boolean> {
        return repo.getTaskResult()
    }

    fun getFailed(): LiveData<LoginFailedState> {
        return repo.getFailed()
    }

}

class LoginFragmentVMFactory(private val repo: FirebaseRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginFragmentVM::class.java)) {
            return LoginFragmentVM(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
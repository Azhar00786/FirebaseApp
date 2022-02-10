package com.example.firebaseapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebaseapp.repository.FirebaseRepo

class HomeFragVM(private val repo: FirebaseRepo) : ViewModel() {
    fun logOutUser() = repo.signOutAcct()

    fun clearAll() {
        //userProfileGot.value=null
        repo.signOutAcct()
        repo.clearAllSignInData()
    }
}

class HomeFragVMFactory(private val repo: FirebaseRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeFragVM::class.java)) {
            return HomeFragVM(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
package com.example.firebaseapp.viewmodels

import androidx.lifecycle.ViewModel

class SharedVM : ViewModel() {
    var verificationCode: String = ""

    fun setVCode(code: String) {
        verificationCode = code
    }

    fun getVCode(): String {
        return verificationCode
    }
}
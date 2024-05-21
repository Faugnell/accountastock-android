package com.example.accountastock_android.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.accountastock_android.data.repository.UserRepository

class LoginViewModel : ViewModel(){

    private val _loginResult = MutableLiveData<Boolean>()
    private val _subscriptionResult = MutableLiveData<String>()
    private val _userIDResult = MutableLiveData<Int>()
    val loginResult: LiveData<Boolean> = _loginResult
    val subscriptionResult: LiveData<String> = _subscriptionResult
    val userIDResult: LiveData<Int> = _userIDResult

    fun login(email: String, password: String) {
        // TODO: Implement real authentication logic
        _loginResult.value = (email == "test" && password == "")
    }

    fun checkSubscription(email: String) {
        _subscriptionResult.value = "level 1"
    }

    fun getUserId(email: String): Int {
        return 1
    }
}

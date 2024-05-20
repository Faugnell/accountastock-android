package com.example.accountastock_android.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    private val _subscriptionResult = MutableLiveData<String>()
    val loginResult: LiveData<Boolean> = _loginResult
    val subscriptionResult: LiveData<String> = _subscriptionResult

    fun login(email: String, password: String) {
        // TODO: Implement real authentication logic
        _loginResult.value = (email == "test" && password == "")
    }

    fun checkSubscription(email: String) {
        _subscriptionResult.value = "level 1"
    }
}

package com.example.accountastock_android.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    fun logout() {
        // TODO: Implement actual logout logic, e.g., clearing user session or tokens
        _logoutEvent.value = true
    }
}

package com.encoding.socialapp.viewmodel.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.encoding.socialapp.repository.LoginRepository
import com.encoding.socialapp.repository.UserRepository

class LoginViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(LoginRepository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}
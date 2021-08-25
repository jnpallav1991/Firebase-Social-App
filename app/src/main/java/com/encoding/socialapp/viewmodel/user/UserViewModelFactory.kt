package com.encoding.socialapp.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.encoding.socialapp.repository.UserRepository

class UserViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(UserRepository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}
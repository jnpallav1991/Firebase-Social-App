package com.encoding.socialapp.viewmodel.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.encoding.socialapp.repository.PostRepository
import com.encoding.socialapp.repository.UserRepository

class PostViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(PostRepository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}
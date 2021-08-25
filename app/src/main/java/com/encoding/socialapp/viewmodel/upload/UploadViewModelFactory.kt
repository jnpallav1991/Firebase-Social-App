package com.encoding.socialapp.viewmodel.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.encoding.socialapp.repository.UploadRepository

class UploadViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(UploadRepository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}
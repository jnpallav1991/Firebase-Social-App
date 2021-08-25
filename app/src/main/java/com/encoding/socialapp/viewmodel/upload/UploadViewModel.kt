package com.encoding.socialapp.viewmodel.upload

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.encoding.socialapp.repository.UploadRepository
import com.encoding.socialapp.utils.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UploadViewModel(private val uploadRepository: UploadRepository) : ViewModel() {

    private var savePostDetails = MutableLiveData<Resource<Uri>>()

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    fun uploadImage(filePath: Uri,extentionImage:String) {
        savePostDetails.postValue(Resource.loading(null))

        val storageReference = storageRef.child("${System.currentTimeMillis()}.${extentionImage}")
        val result = uploadRepository.uploadImage(filePath, storageReference)
       result.continueWithTask {
           if (!it.isSuccessful) {
               it.exception?.let {
                   throw it
               }
           }
           storageReference.downloadUrl
       }
           .addOnCompleteListener {
               if (it.isSuccessful) {
                   val downloadUri = it.result
                   savePostDetails.postValue(Resource.success(downloadUri))
               } else {
                   savePostDetails.postValue(Resource.error(it.exception?.message!!, null))
                   // Handle failures
                   // ...
               }
           }
    }


    fun getUploadImageResult(): LiveData<Resource<Uri>> {
        return savePostDetails
    }


}
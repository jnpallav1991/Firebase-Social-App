package com.encoding.socialapp.viewmodel.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.encoding.socialapp.model.PostDetails
import com.encoding.socialapp.repository.PostRepository
import com.encoding.socialapp.utils.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {

    private var savePostDetails = MutableLiveData<Resource<Boolean>>()

    private var getAllPost = MutableLiveData<Resource<QuerySnapshot>>()

    private val database = Firebase.firestore

    private val auth = Firebase.auth

    fun savePostDetails(postDetails: PostDetails) {
        savePostDetails.postValue(Resource.loading(null))
        val result = postRepository.savePost(postDetails, database)
        if (result != null) {
            result.addOnCompleteListener {
                if (it.isSuccessful) {
                    savePostDetails.postValue(Resource.success(true))
                }
            }
                .addOnFailureListener {
                    savePostDetails.postValue(Resource.error(it.message!!, null))
                }
        } else {
            savePostDetails.postValue(Resource.error("Error", null))
        }
    }

    fun getPost() {
        getAllPost.postValue(Resource.loading(null))
        val result = postRepository.getPost(database)
        if (result != null) {
            result.addOnSuccessListener {
                getAllPost.postValue(Resource.success(it))
            }
                .addOnFailureListener {
                    getAllPost.postValue(Resource.error(it.message!!, null))
                }
        } else {
            getAllPost.postValue(Resource.error("Error", null))
        }
    }

    fun getPostDetailsResponse(): LiveData<Resource<Boolean>> {
        return savePostDetails
    }

    fun getAllPost(): LiveData<Resource<QuerySnapshot>> {
        return getAllPost
    }

}
package com.encoding.socialapp.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.encoding.socialapp.model.UserLogin
import com.encoding.socialapp.repository.UserRepository
import com.encoding.socialapp.utils.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var responseSaveUser = MutableLiveData<Resource<Boolean>>()

    private var getUserDetails = MutableLiveData<Resource<UserLogin>>()

    private val database = Firebase.firestore

    private val auth = Firebase.auth

    fun saveUserDetails(userLogin: UserLogin) {
        responseSaveUser.postValue(Resource.loading(null))
        val result = userRepository.saveUserDetails(userLogin, database, auth)
        if (result!=null) {
            result.addOnCompleteListener {
                responseSaveUser.postValue(Resource.success(true))
            }
                .addOnFailureListener {
                    responseSaveUser.postValue(Resource.error(it.message!!, null))
                }
        }
        else
        {
            responseSaveUser.postValue(Resource.error("Error", null))
        }
    }

    fun getUserDetail() {
        getUserDetails.postValue(Resource.loading(null))
        val result = userRepository.getUserDetails(database, auth)
        if (result!=null) {
            result.addOnSuccessListener { document ->
                if (document != null) {
                    val userLogin = document.toObject(UserLogin::class.java)
                    getUserDetails.postValue(Resource.success(userLogin))
                } else {
                    getUserDetails.postValue(Resource.error("Not Found", null))
                }
            }
                .addOnFailureListener { exception ->
                    getUserDetails.postValue(Resource.error(exception.message!!, null))
                }
        }
        else
        {
            getUserDetails.postValue(Resource.error("Error", null))
        }
    }

    fun getUserSavedResponse(): LiveData<Resource<Boolean>> {
        return responseSaveUser
    }

    fun getUser(): LiveData<Resource<UserLogin>> {
        return getUserDetails
    }

}
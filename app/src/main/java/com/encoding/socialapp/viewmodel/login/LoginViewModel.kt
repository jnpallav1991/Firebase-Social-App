package com.encoding.socialapp.viewmodel.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.encoding.socialapp.model.UserLogin
import com.encoding.socialapp.repository.LoginRepository
import com.encoding.socialapp.utils.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val auth = Firebase.auth

    private val TAG = "LoginViewModel"

    private var createUser = MutableLiveData<Resource<Boolean>>()

    private var sendEmailVerification = MutableLiveData<Resource<Boolean>>()

    private var signInUser = MutableLiveData<Resource<Boolean>>()

    fun createUserWithEmailAndPassword(userLogin: UserLogin) {
        createUser.postValue(Resource.loading(null))
        val result = loginRepository.createUserWithEmailAndPassword(userLogin,auth)
        if (result!=null) {
            result.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    createUser.postValue(Resource.success(true))
                    sendEmailVerification()
                }
                else
                {
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    createUser.postValue(Resource.error(it.exception?.message!!, null))
                }

            }
                .addOnFailureListener {
                    createUser.postValue(Resource.error(it.message!!, null))
                }
        }
        else
        {
            createUser.postValue(Resource.error("Error", null))
        }
    }

    fun signInWithEmailAndPassword(userLogin: UserLogin) {
        signInUser.postValue(Resource.loading(null))
        val result = loginRepository.signInWithEmailAndPassword(userLogin,auth)
        if (result!=null) {
            result.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    signInUser.postValue(Resource.success(true))
                }
                else
                {
                    Log.w(TAG, "signInWithEmail:failure", it.exception)
                    signInUser.postValue(Resource.error(it.exception?.message!!, null))
                }

            }
                .addOnFailureListener {
                    signInUser.postValue(Resource.error(it.message!!, null))
                }
        }
        else
        {
            signInUser.postValue(Resource.error("Error", null))
        }
    }

    private fun sendEmailVerification() {
        sendEmailVerification.postValue(Resource.loading(null))
        val result = loginRepository.sendEmailVerification(auth.currentUser!!)
        if (result!=null) {
            result.addOnCompleteListener {
                if (it.isSuccessful) {

                    sendEmailVerification.postValue(Resource.success(true))
                }
                else
                {
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    sendEmailVerification.postValue(Resource.error(it.exception?.message!!, null))
                }

            }
                .addOnFailureListener {
                    sendEmailVerification.postValue(Resource.error(it.message!!, null))
                }
        }
        else
        {
            sendEmailVerification.postValue(Resource.error("Error", null))
        }
    }

    fun createUserResponse(): LiveData<Resource<Boolean>> {
        return createUser
    }

    fun signInResponse(): LiveData<Resource<Boolean>> {
        return signInUser
    }

    fun sendEmailVerify(): LiveData<Resource<Boolean>> {
        return sendEmailVerification
    }

}
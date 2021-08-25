package com.encoding.socialapp.repository

import com.encoding.socialapp.model.UserLogin
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginRepository {

    fun createUserWithEmailAndPassword(
        userLogin: UserLogin,
        auth: FirebaseAuth
    ): Task<AuthResult>? {

       return auth.createUserWithEmailAndPassword(userLogin.EmailID!!, userLogin.Password!!)
    }

    fun signInWithEmailAndPassword(userLogin: UserLogin, auth: FirebaseAuth): Task<*>? {

        return auth.signInWithEmailAndPassword(userLogin.EmailID!!, userLogin.Password!!)
    }

    fun sendEmailVerification(firebaseUser: FirebaseUser): Task<*>?
    {
        return firebaseUser.sendEmailVerification()
    }
}
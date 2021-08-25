package com.encoding.socialapp.repository

import com.encoding.socialapp.model.UserLogin
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    fun saveUserDetails(
        userLogin: UserLogin,
        database: FirebaseFirestore,
        auth: FirebaseAuth
    ): Task<*>? {

        if (auth.uid != null) {
            return database.collection("Users").document(auth.uid!!)
                .set(userLogin)
        } else {
            return null
        }
    }

    fun getUserDetails(database: FirebaseFirestore, auth: FirebaseAuth): Task<DocumentSnapshot>? {

        if (auth.uid != null) {
            return database.collection("Users")
                .document(auth.uid!!)
                .get()
        }
        else
        {
            return null
        }
    }
}
package com.encoding.socialapp.repository

import com.encoding.socialapp.model.PostDetails
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PostRepository {

    fun savePost(
        postDetails: PostDetails,
        database: FirebaseFirestore
    ): Task<*>? {
        return database.collection("Posts").add(mapOf(
            "name" to postDetails.name,
            "designation" to postDetails.designation,
            "textPost" to postDetails.textPost,
            "postId" to postDetails.postId,
            "imageUrl" to postDetails.imageUrl,
            "created" to FieldValue.serverTimestamp()
        ))
    }

    fun getPost(
        database: FirebaseFirestore
    ): Task<QuerySnapshot>? {
        return database.collection("Posts").get()

    }

}
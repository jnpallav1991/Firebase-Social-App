package com.encoding.socialapp.repository

import android.net.Uri
import com.encoding.socialapp.model.PostDetails
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class UploadRepository {

    fun uploadImage(
        filePath:Uri,
        sRef:StorageReference
    ): UploadTask{
        return sRef.putFile(filePath)


    }


}
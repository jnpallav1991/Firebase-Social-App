package com.encoding.socialapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList


data class PostDetails(
    var name:String? = null,
    var designation:String?=null,
    var textPost: String? = null,
    @ServerTimestamp var created: Timestamp? = null,
    var postId: String? = null,
    var postLikes:ArrayList<PostLike>?=null,
    var postMessages:Long?=null,
    var postIsMessage:Boolean?=null,
    var imageUrl:String?=null
)
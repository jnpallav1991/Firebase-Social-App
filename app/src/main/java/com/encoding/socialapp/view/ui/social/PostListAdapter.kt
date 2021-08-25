package com.encoding.socialapp.view.ui.social

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.encoding.socialapp.R
import com.encoding.socialapp.model.PostDetails
import com.encoding.socialapp.utils.CustomDateUtils
import com.encoding.socialapp.utils.PostStatus
import kotlinx.android.synthetic.main.content_create_dialog.*
import kotlinx.android.synthetic.main.recycler_post_list.view.*


class PostListAdapter(
    private val context: Context,
    private val arrayList: ArrayList<PostDetails>,

    private val sendPostDetails: SendPostDetails
) :
    RecyclerView.Adapter<PostListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(
            R.layout.recycler_post_list, viewGroup,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position], position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(result: PostDetails, position: Int) {

            itemView.txtPost.text = result.textPost
            itemView.txtName.text = result.name
            itemView.txtDesignation.text = result.designation
            val time = result.created

            itemView.txtTime.text = CustomDateUtils.getRelDateTime(context, time!!.toDate())

            if (result.postLikes != null) {
                itemView.txtLikes.text = result.postLikes.toString()
            } else {
                itemView.txtLikes.text = "0 Like"
            }

            if (result.postMessages != null) {
                itemView.txtMessage.text = result.postMessages.toString()
            } else {
                itemView.txtMessage.text = "0 Message"
            }
            if (result.imageUrl!=null)
            {
                itemView.imageUrlPost.visibility = View.VISIBLE
                Glide.with(context)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .load(result.imageUrl)
                    .into(itemView.imageUrlPost)
            }
            else
            {
                itemView.imageUrlPost.visibility = View.GONE
            }

            itemView.ccLike.setOnClickListener {

                sendPostDetails.send(PostStatus.LIKE,result,position)
            }

            itemView.ccMessage.setOnClickListener {
                if (result.postIsMessage != null) {
                    result.postIsMessage = !result.postIsMessage!!
                }
                sendPostDetails.send(PostStatus.COMMENT,result,position)
            }

        }
    }

    interface SendPostDetails {
        fun send(postStatus: PostStatus,result: PostDetails, position: Int)
    }
}
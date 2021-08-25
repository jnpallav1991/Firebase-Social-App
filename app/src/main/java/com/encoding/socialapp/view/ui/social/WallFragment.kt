package com.encoding.socialapp.view.ui.social

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.encoding.socialapp.R
import com.encoding.socialapp.model.PostDetails
import com.encoding.socialapp.utils.PostStatus
import com.encoding.socialapp.utils.Status
import com.encoding.socialapp.viewmodel.post.PostViewModel
import com.encoding.socialapp.viewmodel.post.PostViewModelFactory
import kotlinx.android.synthetic.main.fragment_social.*


class WallFragment : Fragment(), PostListAdapter.SendPostDetails {

    private lateinit var postListAdapter: PostListAdapter
    private lateinit var postList: ArrayList<PostDetails>
    private lateinit var postViewModel: PostViewModel
    private val TAG = "WallFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = RecyclerView.VERTICAL
        val verticalDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val verticalDivider =
            ContextCompat.getDrawable(requireContext(), R.drawable.vertical_divider)
        verticalDecoration.setDrawable(verticalDivider!!)
        recyclerPost.addItemDecoration(verticalDecoration)
        recyclerPost.layoutManager = layoutManager

        postList = ArrayList()
        postListAdapter = PostListAdapter(requireContext(), postList, this)
        recyclerPost.adapter = postListAdapter

        etWriteSomething.setOnClickListener {

            val intent = Intent(requireContext(), PostActivity::class.java)
            startActivity(intent)
        }
        setupObserver()
        postViewModel.getPost()

        swipePost.setOnRefreshListener {
            postViewModel.getPost()
        }

    }

    private fun setupViewModel() {
        postViewModel = ViewModelProviders.of(
            this, PostViewModelFactory()
        ).get(PostViewModel::class.java)
    }

    private fun setupObserver() {
        postViewModel.getAllPost().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    swipePost.isRefreshing= false
                    it.data?.let { querySnapshot ->
                        loading.visibility = View.GONE
                        postList.clear()

                        for (document in querySnapshot) {
                            val postDetail = document.toObject(PostDetails::class.java)
                            postList.add(postDetail)
                            //Log.d("TAG", "${document.id} => ${document.data}")
                        }
                        postListAdapter.notifyDataSetChanged()

                    }
                }
                Status.LOADING -> {
                    swipePost.isRefreshing= false
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    swipePost.isRefreshing= false
                    loading.visibility = View.GONE
                    Log.w(TAG, "Error getting documents:failure" + it.message)
                }
            }

        })
    }

    override fun send(postStatus: PostStatus, result: PostDetails, position: Int) {
        when (postStatus) {
            PostStatus.LIKE->
            {

            }
            PostStatus.COMMENT->
            {

            }
        }
    }
}
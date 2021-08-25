package com.encoding.socialapp.view.ui.social

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.encoding.socialapp.R
import com.encoding.socialapp.model.PostDetails
import com.encoding.socialapp.utils.SharedPreferenceUtil
import com.encoding.socialapp.utils.Status
import com.encoding.socialapp.utils.compressImageFile
import com.encoding.socialapp.viewmodel.post.PostViewModel
import com.encoding.socialapp.viewmodel.post.PostViewModelFactory
import com.encoding.socialapp.viewmodel.upload.UploadViewModel
import com.encoding.socialapp.viewmodel.upload.UploadViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.content_create_dialog.*
import kotlinx.android.synthetic.main.dialog_create_post.*
import kotlinx.android.synthetic.main.fragment_item_list_dialog_list_dialog.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED =
    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

class PostActivity : AppCompatActivity(), BottomItemsAdapter.SendSelectedType {

    companion object {
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private var sheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private val FINAL_CHOOSE_PHOTO = 2
    private var imageUri: Uri? = null
    private var filePathUri: Uri? = null
    private var queryImageUrl: String = ""
    private var imgPath: String = ""
    private lateinit var auth: FirebaseAuth
    private var TAG = "PostActivity"
    private var isImageSelected = false

    private lateinit var postViewModel: PostViewModel
    private lateinit var uploadViewModel: UploadViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        setupViewModel()

        auth = Firebase.auth
        toolbarDialog.setNavigationOnClickListener({ v ->
            finish()
        })

        toolbarDialog.title = "Create Post"
        toolbarDialog.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.colorWhite
            )
        )
        toolbarDialog.inflateMenu(R.menu.toolbar_dialog_menu)
        toolbarDialog.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.btnSave -> {

                    savePostDetails()
                }
            }
            true
        }

        val name = SharedPreferenceUtil(this).getString("Name", "")

        etName.text = name

        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior!!.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.e("Drag", "Hidden")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.e("Drag", "Expand")

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.e("Drag", "Collapse")

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.e("Drag", "Dragging")

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                    else -> {

                    }
                }
            }

        })

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        val verticalDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val verticalDivider =
            ContextCompat.getDrawable(this, R.drawable.vertical_divider)
        verticalDecoration.setDrawable(verticalDivider!!)
        bottomSheetList.addItemDecoration(verticalDecoration)
        bottomSheetList.layoutManager = layoutManager

        val list: ArrayList<String> = ArrayList()
        list.add("Photos")
        list.add("Videos")

        val productListAdapter = BottomItemsAdapter(list, this)
        bottomSheetList.adapter = productListAdapter

        if (sheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        /*if (sheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior!!.setHideable(true);
            sheetBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN);
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }*/
        edPost.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                sheetBehavior!!.setHideable(true);
                sheetBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN);
                v.performClick()
            }
            false
        })

        txtAddPost.setOnClickListener {
            if (sheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        setupObserver()
    }

    private fun setupObserver() {

        //signIn username and password
        postViewModel.getPostDetailsResponse().observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let { isSuccess ->
                        loading.visibility = View.GONE
                        if (isSuccess) {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                            finish()
                        } else {


                        }
                    }
                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    loading.visibility = View.GONE
                    Log.w(TAG, "Error writing document:failure" + it.message)
                }
            }

        })

        uploadViewModel.getUploadImageResult().observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let { imageUri ->
                        loading.visibility = View.GONE
                        uploadPost(imageUri)
                    }
                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    loading.visibility = View.GONE
                    Log.w(TAG, "Error writing document:failure" + it.message)
                }
            }

        })

    }

    private fun setupViewModel() {
        postViewModel = ViewModelProviders.of(
            this, PostViewModelFactory()
        ).get(PostViewModel::class.java)
        uploadViewModel = ViewModelProviders.of(
            this, UploadViewModelFactory()
        ).get(UploadViewModel::class.java)
    }

    private fun savePostDetails() {

        try {

            if (isImageSelected)
            {
                var file = Uri.fromFile(File(queryImageUrl))
                uploadViewModel.uploadImage(file!!,getFileExtension(filePathUri)!!)
            }
            else {
               uploadPost(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun uploadPost(imageUri:Uri?)
    {
        val postDetails = PostDetails()
        postDetails.name = SharedPreferenceUtil(this).getString("Name", "")
        postDetails.designation = SharedPreferenceUtil(this).getString("Designation", "")
        //postDetails.postDate = FieldValue.serverTimestamp()
        postDetails.postId = auth.uid
        if (imageUri!=null)
        {
            postDetails.imageUrl = imageUri.toString()
            postViewModel.savePostDetails(postDetails)
        }
        else
        {
            if (edPost.text.toString().trim().isNotEmpty()) {
                postDetails.textPost = edPost.text.toString().trim()
                postViewModel.savePostDetails(postDetails)
            }
            else
            {
                Toast.makeText(this,"Write something on wall",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getFileExtension(uri: Uri?): String? {
        if (uri!=null) {
            val mime = MimeTypeMap.getSingleton()
            return mime.getExtensionFromMimeType(contentResolver.getType(uri))
        }
        else
        {
            return ".jpg"
        }
    }

    override fun send(result: String, position: Int) {
        if (position == 0) {
            if (hasPermissions(this)) {
                openAlbum()
            } else {
                requestPermissions(
                    PERMISSIONS_REQUIRED,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else if (position == 1) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            FINAL_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageRequest(data)
                    }
                }
        }
    }

    private fun handleImageRequest(data: Intent?) {
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
            //progressBar.visibility = View.GONE
            Toast.makeText(
                this,
                t.localizedMessage ?: "eror",
                Toast.LENGTH_SHORT
            ).show()
        }

        GlobalScope.launch(Dispatchers.Main + exceptionHandler) {
            //progressBar.visibility = View.VISIBLE

            if (data?.data != null) {
                filePathUri = data.data//Photo from gallery
                imageUri = data.data
                queryImageUrl = imageUri?.path!!
                queryImageUrl = compressImageFile(queryImageUrl, false, imageUri!!)
            } else {
                queryImageUrl = imgPath ?: ""
                compressImageFile( queryImageUrl, uri = imageUri!!)
            }
            imageUri = Uri.fromFile(File(queryImageUrl))

            if (queryImageUrl.isNotEmpty()) {

                isImageSelected = true
                Glide.with(this@PostActivity)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .load(queryImageUrl)
                    .into(imgPost)

                imgPost.visibility = View.VISIBLE
            }
            //progressBar.visibility = View.GONE
        }

    }


    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(this, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion
                )
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(
                        "content://downloads/public_downloads"
                    ), java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            imagePath = uri.path
        }
        renderImage(imagePath)
    }

    private fun renderImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imgPost?.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "ImagePath is null", Toast.LENGTH_SHORT).show()

        }
    }

    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, FINAL_CHOOSE_PHOTO)
    }

}
package com.encoding.socialapp.view.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.encoding.socialapp.R
import com.encoding.socialapp.model.UserLogin
import com.encoding.socialapp.utils.Constant
import com.encoding.socialapp.utils.Status
import com.encoding.socialapp.viewmodel.login.LoginViewModel
import com.encoding.socialapp.viewmodel.login.LoginViewModelFactory
import com.encoding.socialapp.viewmodel.user.UserViewModel
import com.encoding.socialapp.viewmodel.user.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_signup.*

class SignUpFragment : Fragment() {

    private val TAG = "SignupFragment"
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)// Initialize Firebase Auth
        setupViewModel()
        auth = Firebase.auth

    }

    private fun setupViewModel() {
        userViewModel = ViewModelProviders.of(
            this, UserViewModelFactory()
        ).get(UserViewModel::class.java)

        loginViewModel = ViewModelProviders.of(
            this, LoginViewModelFactory()
        ).get(LoginViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignUp.setOnClickListener {

            if (validateUser()) {
                Constant.hideKeyBoard(requireContext(), it)
                val email = edEmail.text.toString()
                val password = edPassword.text.toString()
                val userLogin  = UserLogin()
                userLogin.EmailID = email
                userLogin.Password= password
                loginViewModel.createUserWithEmailAndPassword(userLogin)
            }
        }

        setupObserver()
    }

    private fun setupObserver() {

        //save user profile to firestore
        userViewModel.getUserSavedResponse().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    loading.visibility = View.GONE
                    it.data?.let { isSuccess ->
                        if (isSuccess)
                        {
                            NavHostFragment.findNavController(this)
                                .navigate(SignUpFragmentDirections.actionSignupFragmentToLoginFragment())
                        }
                    }

                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    loading.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        //create username and password
        loginViewModel.createUserResponse().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    loading.visibility = View.GONE
                    it.data?.let { isSuccess ->
                        if (isSuccess)
                        {
                            //Log.d(TAG, "createUserWithEmail:success")
                            //sendEmailVerification()
                        }
                    }

                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    loading.visibility = View.GONE
                    Log.w(TAG, "createUserWithEmail:failure"+ it.message)
                    try {
                        Constant.alertDialog(requireContext(), it.message!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

        //send email verification
        loginViewModel.sendEmailVerify().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    loading.visibility = View.GONE
                    it.data?.let { isSuccess ->
                        if (isSuccess)
                        {
                            try {
                                val user = auth.currentUser!!
                                Constant.alertDialog(
                                    requireContext(),
                                    "Verification email sent to ${user.email}"
                                )
                                storeUserData()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        else
                        {
                            Log.e(TAG, "sendEmailVerification"+ it.message)
                            try {
                                Constant.alertDialog(requireContext(), it.message!!)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    loading.visibility = View.GONE
                    Log.w(TAG, "createUserWithEmail:failure"+ it.message)
                    try {
                        Constant.alertDialog(requireContext(), it.message!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }


    private fun storeUserData() {

        val userLogin = UserLogin()
        userLogin.EmailID = edEmail.text.toString()
        userLogin.name = edFirstName.text.toString()
        userLogin.designation = edDesignation.text.toString()
        userViewModel.saveUserDetails(userLogin)
    }

    private fun validateUser(): Boolean {
        if (edEmail.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT).show()
            return false
        } else if (edPassword.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        } else if (edFirstName.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter first name", Toast.LENGTH_SHORT).show()
            return false
        } else if (edDesignation.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter designation", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
package com.encoding.socialapp.view.ui.login

import android.content.Context
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
import com.encoding.socialapp.utils.OnFragmentInteractionListener
import com.encoding.socialapp.utils.SharedPreferenceUtil
import com.encoding.socialapp.utils.Status
import com.encoding.socialapp.viewmodel.login.LoginViewModel
import com.encoding.socialapp.viewmodel.login.LoginViewModelFactory
import com.encoding.socialapp.viewmodel.user.UserViewModel
import com.encoding.socialapp.viewmodel.user.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.loading
import kotlinx.android.synthetic.main.fragment_signup.*

class LoginFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var loginViewModel: LoginViewModel
    private val TAG = "LoginFragment"
    private lateinit var auth: FirebaseAuth
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        btnCreateAccount.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
        }

        btnLogin.setOnClickListener {

            if (validateUser()) {
                Constant.hideKeyBoard(requireContext(), it)
                val email = email.text.toString()
                val password = password.text.toString()
                if (switchRemember.isChecked) {
                    SharedPreferenceUtil(requireContext()).setBoolean("RememberMe", true)
                }
                val userLogin  = UserLogin()
                userLogin.EmailID = email
                userLogin.Password= password
                loginViewModel.signInWithEmailAndPassword(userLogin)
            }
        }

        setupObserver()
    }

    private fun setupViewModel() {
        userViewModel = ViewModelProviders.of(
            this, UserViewModelFactory()
        ).get(UserViewModel::class.java)

        loginViewModel = ViewModelProviders.of(
            this, LoginViewModelFactory()
        ).get(LoginViewModel::class.java)

    }

    private fun setupObserver() {

        //signIn username and password
        loginViewModel.signInResponse().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let { isSuccess ->
                        if (isSuccess)
                        {
                            val user = auth.currentUser
                            if (user!!.isEmailVerified) {
                                userViewModel.getUserDetail()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Verification email sent to ${user.email}. Please verify",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else
                        {
                            loading.visibility = View.GONE
                            // If sign in fails, display a message to the user.
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

        //Get user details
        userViewModel.getUser().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    loading.visibility = View.GONE
                    it.data?.let { userLogin ->

                        SharedPreferenceUtil(requireContext()).setString(
                            "Name",
                            "${userLogin?.name}"
                        )
                        SharedPreferenceUtil(requireContext()).setString(
                            "Designation",
                            "${userLogin?.designation}"
                        )
                        NavHostFragment.findNavController(this)
                            .navigate(LoginFragmentDirections.actionLoginFragmentToSocialFragment())
                        listener!!.signInSuccess()
                    }

                }
                Status.LOADING -> {
                    loading.visibility = View.VISIBLE

                }
                Status.ERROR -> {
                    loading.visibility = View.GONE
                    //Handle Error
                }
            }
        })
    }

    private fun validateUser(): Boolean {
        if (email.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.text.toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + "implement OnFragmentInteractionListener")
        }
    }

}
package com.encoding.socialapp.view.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.encoding.socialapp.R
import com.encoding.socialapp.utils.OnFragmentInteractionListener
import com.encoding.socialapp.utils.SharedPreferenceUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {

    private lateinit var navHostFragment: NavHostFragment

    private lateinit var navController: NavController

    private lateinit var navGraph: NavGraph

    private lateinit var auth: FirebaseAuth

    private var settingIconVisibility = true

    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        auth = Firebase.auth

        //navController = findNavController(R.id.nav_host_fragment)
        navHostFragment = nav_host_fragment as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.wallFragment,
                R.id.createPostFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottom_nav_view.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.loginFragment -> {
                    toolbar.visibility = View.GONE
                    bottom_nav_view.visibility = View.GONE
                }
                R.id.signupFragment -> {
                    settingIconVisibility = false
                    toolbar.visibility = View.VISIBLE
                    bottom_nav_view.visibility = View.GONE
                    invalidateOptionsMenu()
                }
                R.id.settingFragment -> {
                    settingIconVisibility = true
                    bottom_nav_view.visibility = View.GONE
                    toolbar.visibility = View.VISIBLE
                    invalidateOptionsMenu()
                }
                else -> {
                    settingIconVisibility = true
                    bottom_nav_view.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    invalidateOptionsMenu()
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        ).navigateUp() || super.onSupportNavigateUp()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        val settingIcon = menu!!.findItem(R.id.settingFragment)
        settingIcon.isVisible = settingIconVisibility

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            NavHostFragment.findNavController(navHostFragment)
        )
                || super.onOptionsItemSelected(item)

    }

    override fun onDestroy() {
        super.onDestroy()
        val remember = SharedPreferenceUtil(this).getBoolean("RememberMe", false)
        if (!remember) {
            auth.signOut()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                navGraph.startDestination = R.id.wallFragment
                navController.graph = navGraph
            }
        }
    }

    override fun signOutSuccess() {
        navGraph.startDestination = R.id.loginFragment
        navController.graph = navGraph
    }

    override fun signInSuccess() {
        navGraph.startDestination = R.id.wallFragment
        navController.graph = navGraph
    }
}
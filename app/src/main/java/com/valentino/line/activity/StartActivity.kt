package com.valentino.line.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.valentino.line.R
import com.valentino.line.StartPagerAdapter
import kotlinx.android.synthetic.main.activity_start.*
import android.widget.LinearLayout
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.valentino.line.dao.UserDAO
import java.util.*

class StartActivity : AppCompatActivity(), View.OnClickListener, ViewPager.OnPageChangeListener {
    private val TAG = StartActivity::class.java.simpleName

    private val imageResources = intArrayOf(R.drawable.start_1, R.drawable.start_2,
    R.drawable.start_3, R.drawable.start_4, R.drawable.start_5)
    private var dotsCount = 0
    lateinit private var dots : Array<ImageView>
    private var loginManager : LoginManager = LoginManager.getInstance()
    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var callbackManager : CallbackManager = CallbackManager.Factory.create()

    override fun onStart() {
        super.onStart()
        if (AccessToken.getCurrentAccessToken() != null && firebaseAuth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            AccessToken.setCurrentAccessToken(null)
            //goToMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        startViewPager.adapter = StartPagerAdapter(applicationContext, imageResources)
        setupViewPagerIndicator()
        startViewPager.addOnPageChangeListener(this)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        loginWithFacebookWrapper.setOnClickListener(this)
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Success")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "Cancel")
            }

            override fun onError(e: FacebookException) {
                Log.d(TAG, "Error" + e.localizedMessage)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupViewPagerIndicator() {
        dotsCount = startViewPager.adapter?.getCount() ?: 0
        dots = Array<ImageView>(dotsCount) {ImageView(this)}

        for (i in 0 until dotsCount) {
            dots[i].setImageResource(R.drawable.shape_pager_deselected)
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 20, 40)
            pagerIndicator.addView(dots[i], params)
        }
        dots[0].setImageResource(R.drawable.shape_pager_selected)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun goToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            loginWithFacebookWrapper -> {
                loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        for (i in 0 until dotsCount) {
            dots[i].setImageResource(R.drawable.shape_pager_deselected)
        }
        dots[position].setImageResource(R.drawable.shape_pager_selected)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "signInWithCredential:success")
                        UserDAO.getUser {
                            if (it != null) {
                                Log.d(TAG, it.name)
                                goToMainActivity()
                            }
                            else {
                                goToRegisterActivity()
                            }
                        }
                        //UserDAO.setFriendsList()
                    } else {
                        Log.w("Login", "signInWithCredential:failure", task.exception)
                    }
                }
    }
}

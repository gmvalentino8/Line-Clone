package com.valentino.line.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.valentino.line.R
import com.valentino.line.dao.UserDAO
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        UserDAO.getUserDataFromFacebook( {firstName : String, lastName : String, picture : String ->
            displayNameEditText.setText(firstName + " " + lastName)
            Glide.with(this).load(picture).into(profileImageView)
        })
    }
}

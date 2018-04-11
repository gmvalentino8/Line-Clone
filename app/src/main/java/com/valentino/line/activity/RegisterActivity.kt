package com.valentino.line.activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.valentino.line.R
import com.valentino.line.dao.UserDAO.getUserDataFromFacebook
import com.valentino.line.dao.UserDAO.postUser
import com.valentino.line.dao.UserDAO.saveProfileImage
import com.valentino.line.model.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getUserDataFromFacebook( {firstName: String, lastName: String,
                                          email: String, picture: String ->
            displayNameEditText.setText(firstName + " " + lastName)
            this.email = email
            Glide.with(this).load(picture).into(profileImageView)
        })
        profileImageView.setOnClickListener(this)
        registerButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            profileImageView -> {
                val pickPhoto = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 0)
            }
            registerButton -> {
                val user = User(null, email, displayNameEditText.text.toString())
                postUser(user)
                saveProfileImage(profileImageView)
                goToMainActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                if (resultCode == RESULT_OK) {
                    val selectedImage = data?.data as Uri
                    profileImageView.setImageURI(selectedImage)
                }
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}

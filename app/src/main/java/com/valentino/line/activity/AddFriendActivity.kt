package com.valentino.line.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import com.valentino.line.R
import kotlinx.android.synthetic.main.activity_add_friend.*
import android.view.View
import com.valentino.line.dao.UserDAO
import com.valentino.line.model.User


class AddFriendActivity : AppCompatActivity() {

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        searchEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                user = null
                if (searchEditText.text.toString() == UserDAO.currentUser?.email!!) {
                    userNotFound()
                    return@OnKeyListener false
                }
                UserDAO.getUserFromEmail(searchEditText.text.toString(),
                        { displaySearchResult(it) },
                        { userNotFound() }
                )
                return@OnKeyListener true
            }
            false
        })
        addButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                UserDAO.postFriend(user)
                finish()
            }
        })
    }

    private fun userNotFound() {
        if (user == null) {
            foundLayout.visibility = View.GONE
            notFoundTextView.visibility = View.VISIBLE
        }
    }

    private fun displaySearchResult(user: User?) {
        this.user = user
        notFoundTextView.visibility = View.GONE
        UserDAO.loadProfileImage(this, user?.uid!!, profileImageView)
        nameTextView.text = user.name
        foundLayout.visibility = View.VISIBLE
    }
}

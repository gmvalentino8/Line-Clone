package com.valentino.line.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

import com.valentino.line.R
import com.valentino.line.activity.StartActivity
import kotlinx.android.synthetic.main.fragment_more.view.*

class MoreFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)
        view.signoutButton.setOnClickListener({
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            AccessToken.setCurrentAccessToken(null)
            val intent = Intent(context, StartActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        })
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_more_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}

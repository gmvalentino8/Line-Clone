package com.valentino.line.activity

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.valentino.line.R
import com.valentino.line.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TabHost



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        tabHost.setup(this, supportFragmentManager, android.R.id.tabcontent)
        tabHost.addTab(tabHost.newTabSpec("Friends").setIndicator("", resources.getDrawable(R.drawable.ic_friends_tab)), FriendsFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("Chats").setIndicator("", resources.getDrawable(R.drawable.ic_chats_tab)), ChatsFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("Timeline").setIndicator("", resources.getDrawable(R.drawable.ic_timeline_tab)), TimelineFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("Calls").setIndicator("", resources.getDrawable(R.drawable.ic_calls_tab)), CallsFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("More").setIndicator("", resources.getDrawable(R.drawable.ic_more_tab)), MoreFragment::class.java, null)
        tabHost.setBackgroundColor(Color.WHITE)
        if (intent.hasExtra("fragment")) {
            tabHost.setCurrentTabByTag("Chats")
        }
    }
}

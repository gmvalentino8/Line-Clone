package com.valentino.line.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.valentino.line.R
import com.valentino.line.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.ContextCompat
import android.widget.TabHost



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        tabHost.setup(this, supportFragmentManager, android.R.id.tabcontent)
//        tabHost.setOnTabChangedListener( {
//            //this change previous selected tab color to default color
//            for (i in 0 until tabHost.tabWidget.childCount)
//                tabHost.tabWidget.getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
//            //this line change selected tab color
//            tabHost.tabWidget.getChildAt(tabHost.currentTab).setBackgroundColor(ContextCompat.getColor(this, R.color.white))
//        })
        tabHost.addTab(tabHost.newTabSpec("Friends").setIndicator("", resources.getDrawable(R.drawable.ic_friends_icon)), FriendsFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("Chats").setIndicator("", resources.getDrawable(R.drawable.ic_chats_icon)), ChatsFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("Timeline").setIndicator("", resources.getDrawable(R.drawable.ic_timeline_icon)), TimelineFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("Calls").setIndicator("", resources.getDrawable(R.drawable.ic_calls_icon)), CallsFragment::class.java, null)
        tabHost.addTab(tabHost.newTabSpec("More").setIndicator("", resources.getDrawable(R.drawable.ic_more_icon)), MoreFragment::class.java, null)
    }
}

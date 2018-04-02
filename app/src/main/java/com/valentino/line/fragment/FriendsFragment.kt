package com.valentino.line.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.valentino.line.adapter.FriendsListAdapter

import com.valentino.line.R
import com.valentino.line.activity.AddFriendActivity
import com.valentino.line.dao.UserDAO
import com.valentino.line.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_friends.view.*

class FriendsFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FriendsListAdapter
    var friendsData = arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.title = "Friends"
        UserDAO.getFriends {
            if (it != null) {
                friendsData.add(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_friends, container, false)
        linearLayoutManager = LinearLayoutManager(context)
        view.friendsRecyclerView.layoutManager = linearLayoutManager
        adapter = FriendsListAdapter(friendsData)
        view.friendsRecyclerView.adapter = adapter
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_friends_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addFriend -> {
                val intent = Intent(context, AddFriendActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

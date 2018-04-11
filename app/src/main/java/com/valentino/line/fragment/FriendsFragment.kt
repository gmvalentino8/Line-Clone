package com.valentino.line.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import com.valentino.line.adapter.FriendsListAdapter

import com.valentino.line.R
import com.valentino.line.activity.AddFriendActivity
import com.valentino.line.activity.ChatActivity
import com.valentino.line.dao.ChatDAO
import com.valentino.line.dao.MessageDAO
import com.valentino.line.dao.UserDAO
import com.valentino.line.listener.ItemTouchListener
import com.valentino.line.model.Chat
import com.valentino.line.model.ChatMetadata
import com.valentino.line.model.Message
import com.valentino.line.model.User
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
        view.friendsRecyclerView.addOnItemTouchListener(ItemTouchListener(context, view.friendsRecyclerView, object : ItemTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {
                var intent = Intent(context, ChatActivity::class.java)
                ChatDAO.getChatWithUser(friendsData.get(position).uid!!) {
                    Log.d("Get Chats", friendsData.get(position).uid!!)
                    ChatDAO.getChatMetadata(it!!) {
                        if (it.message == null) {
                            it.message = Message()
                        }
                        intent.putExtra("ChatMetadata", it)
                        startActivity(intent)
                    }
                }
            }

            override fun onLongClick(view: View?, position: Int) {
                Toast.makeText(view?.context, "Long Click", Toast.LENGTH_SHORT).show()
            }
        }))
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

package com.valentino.line.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.google.firebase.auth.FirebaseAuth

import com.valentino.line.R
import com.valentino.line.activity.ChatActivity
import com.valentino.line.adapter.ChatsListAdapter
import com.valentino.line.adapter.FriendsListAdapter
import com.valentino.line.dao.ChatDAO
import com.valentino.line.listener.ItemTouchListener
import com.valentino.line.model.Chat
import com.valentino.line.model.ChatMetadata
import com.valentino.line.model.User
import kotlinx.android.synthetic.main.fragment_chats.view.*
import kotlinx.android.synthetic.main.fragment_friends.view.*

class ChatsFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ChatsListAdapter
    var chatsMetadata = arrayListOf<ChatMetadata>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onResume() {
        super.onResume()
        loadChats()
        activity?.title = "Chats"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_chats, container, false)
        linearLayoutManager = LinearLayoutManager(context)
        view.chatsRecyclerView.layoutManager = linearLayoutManager
        adapter = ChatsListAdapter(chatsMetadata)
        view.chatsRecyclerView.adapter = adapter
        view.chatsRecyclerView.addOnItemTouchListener(ItemTouchListener(context, view.chatsRecyclerView, object : ItemTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {
                var intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("ChatMetadata", chatsMetadata[position])
                startActivity(intent)
            }

            override fun onLongClick(view: View?, position: Int) {
            }

        }))
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_chats_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun loadChats() {
        chatsMetadata.clear()
        ChatDAO.getUserChats({
            if (it != null) {
                ChatDAO.getChatMetadata(it) {
                    if (it.message != null) {
                        chatsMetadata.add(it)
                    }
                    chatsMetadata.sortWith(Comparator { p0, p1 ->
                        when {
                            p0?.message?.time!! < p1?.message?.time!! -> 1
                            else -> -1
                        }
                    })
                    adapter.notifyDataSetChanged()
                }
            }
        }, {
            if (it != null) {
                ChatDAO.getChatMetadata(it) {
                    if (it.message != null) {
                        for (item in chatsMetadata) {
                            if (item.chat?.cid == it.chat?.cid) {
                                chatsMetadata.remove(item)
                            }
                        }
                        chatsMetadata.add(0, it)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

}

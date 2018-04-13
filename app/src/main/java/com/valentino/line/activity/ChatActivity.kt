package com.valentino.line.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.valentino.line.R
import com.valentino.line.adapter.ChatsListAdapter
import com.valentino.line.adapter.MessagesAdapter
import com.valentino.line.dao.MessageDAO
import com.valentino.line.dao.UserDAO
import com.valentino.line.model.ChatMetadata
import com.valentino.line.model.Message
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_chats.view.*
import java.util.*
import kotlin.Comparator

class ChatActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var chatMetadata : ChatMetadata
    private lateinit var adapter: MessagesAdapter
    private var messages = arrayListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(chatToolbar)
        chatMetadata = intent.getParcelableExtra<ChatMetadata>("ChatMetadata")
        toolbarTitle.text = chatMetadata.partner?.name
        toolbarBack.setOnClickListener(this)
        sendButton.setOnClickListener(this)
        addFriendButton.setOnClickListener(this)

        linearLayoutManager = LinearLayoutManager(this)
        messagesRecyclerView.layoutManager = linearLayoutManager
        adapter = MessagesAdapter(messages, "")
        messagesRecyclerView.adapter = adapter

        UserDAO.getFriend(chatMetadata.partner?.uid!!) {
            addFriendLayout.visibility = View.VISIBLE
        }

        MessageDAO.getMessagesFromChat(chatMetadata.chat?.cid!!) {
            messages.add(it!!)
            messages.sortWith(Comparator { p0, p1 ->
                when {
                    p0?.time!! > p1?.time!! -> 1
                    else -> -1
                }
            })
            adapter.notifyDataSetChanged()
            messagesRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }

        //MessageDAO.postReadMessages((chatMetadata.chat?.cid!!))

        setupInputEditText()
    }

    private fun setupInputEditText() {
        messagesRecyclerView.addOnLayoutChangeListener { p0, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom && adapter.itemCount > 0) {
                val lastItem = adapter.itemCount - 1
                messagesRecyclerView.post {
                    messagesRecyclerView.smoothScrollToPosition(lastItem)
                }
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (inputEditText.text.isEmpty()) {
                    sendButton.visibility = View.GONE
                    speechButton.visibility = View.VISIBLE

                } else {
                    speechButton.visibility = View.GONE
                    sendButton.visibility = View.VISIBLE
                }
            }
        })

        inputEditText.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, p1: Boolean) {
                if (p1) {
                    inputOptionsLayout.visibility = View.GONE
                    expandButton.visibility = View.VISIBLE
                } else {
                    inputOptionsLayout.visibility = View.VISIBLE
                    expandButton.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_options, menu)
        return true
    }



    override fun onClick(p0: View?) {
        when (p0) {
            sendButton -> {
                val message = Message(null, FirebaseAuth.getInstance().currentUser?.uid!!, inputEditText.text.toString(), Date().time)
                MessageDAO.postMessage(message, chatMetadata.chat!!)
                inputEditText.setText("")
            }
            toolbarBack -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragment", "chat")
                intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            addFriendButton -> {
                addFriendLayout.visibility = View.GONE
                UserDAO.postFriend(chatMetadata.partner)
            }
        }
    }

}

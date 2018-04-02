package com.valentino.line.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.valentino.line.R
import com.valentino.line.adapter.ChatsListAdapter
import com.valentino.line.adapter.MessagesAdapter
import com.valentino.line.dao.MessageDAO
import com.valentino.line.model.ChatMetadata
import com.valentino.line.model.Message
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_chats.view.*
import java.util.*

class ChatActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var chatMetadata : ChatMetadata
    private lateinit var adapter: MessagesAdapter
    var messages = arrayListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatMetadata = intent.getParcelableExtra<ChatMetadata>("ChatMetadata")
        chatToolbar.title = chatMetadata.partner?.name
        linearLayoutManager = LinearLayoutManager(this)
        messagesRecyclerView.layoutManager = linearLayoutManager
        adapter = MessagesAdapter(messages, "")
        messagesRecyclerView.adapter = adapter
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
                }
                else {
                    inputOptionsLayout.visibility = View.VISIBLE
                    expandButton.visibility = View.GONE
                }
            }
        }
        sendButton.setOnClickListener(this)
        MessageDAO.getMessagesFromChat(chatMetadata.chat?.cid!!) {
            messages.add(it!!)
            adapter.notifyDataSetChanged()
        }

    }

    override fun onClick(p0: View?) {
        when (p0) {
            sendButton -> {
                val message = Message(null, MessageDAO.currentUser?.uid!!, inputEditText.text.toString(), Date().time)
                MessageDAO.postMessage(message, chatMetadata.chat?.cid!!)
                inputEditText.setText("")
            }
        }
    }

}

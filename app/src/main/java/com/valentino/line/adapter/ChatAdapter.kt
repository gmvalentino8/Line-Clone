package com.valentino.line.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.valentino.line.R
import com.valentino.line.dao.MessageDAO
import com.valentino.line.dao.UserDAO
import com.valentino.line.model.Message
import com.valentino.line.model.ChatMetadata

import kotlinx.android.synthetic.main.item_chat.view.*
import java.text.DateFormat
import java.util.*

/**
 * Created by Valentino on 4/2/18.
 */

class MessagesAdapter(private val messagesDataSet: List<Message>, private val partnerId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return  messagesDataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return MessagesViewHolder(root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = messagesDataSet[position]
        val placeHolder = holder as MessagesViewHolder
        placeHolder.bindData(data)
    }

    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: View = itemView

        fun bindData(message: Message) {
            view.messageTextView.text = message.content
        }
    }
}



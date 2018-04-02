package com.valentino.line.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.valentino.line.R
import com.valentino.line.dao.MessageDAO
import com.valentino.line.dao.UserDAO
import com.valentino.line.model.Chat
import com.valentino.line.model.ChatMetadata

import kotlinx.android.synthetic.main.item_chat.view.*
import java.text.DateFormat
import java.util.*

/**
 * Created by Valentino on 4/2/18.
 */

class ChatsListAdapter(private val chatsDataSet: List<ChatMetadata>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return  chatsDataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatsListViewHolder(root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = chatsDataSet[position]
        val placeHolder = holder as ChatsListViewHolder
        placeHolder.bindData(data)
    }

    class ChatsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: View = itemView

        fun bindData(chatMeta: ChatMetadata) {
            view.messageTextView.text = chatMeta.message?.content
            val date = DateFormat.getDateInstance(DateFormat.SHORT).format(Date(chatMeta.message?.time!!))
            view.dateTextView.text = date
            view.nameTextView.text = chatMeta.partner?.name
            UserDAO.loadProfileImage(view.context, chatMeta.partner?.uid!!, view.profileImageView)
        }
    }
}



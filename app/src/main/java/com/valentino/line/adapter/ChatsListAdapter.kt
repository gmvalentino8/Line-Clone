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

import kotlinx.android.synthetic.main.item_list_chat.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Valentino on 4/2/18.
 */

class ChatsListAdapter(private val chatsDataSet: List<ChatMetadata>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return  chatsDataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat, parent, false)
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
            val date = Date(chatMeta.message?.time!!)
            view.dateTextView.text = when (getDateStatus(date)) {
                1 -> {
                    "Yesterday"
                }
                2 -> {
                    val dateFormat = SimpleDateFormat("M/d")
                    dateFormat.format(date)
                }
                3 -> {
                    val dateFormat = SimpleDateFormat("M/dd/yyyy")
                    dateFormat.format(date)
                }
                else -> {
                    val dateFormat = SimpleDateFormat("h:mm a")
                    dateFormat.format(date)
                }
            }
            view.nameTextView.text = chatMeta.partner?.name
            val unread = chatMeta.chat?.userMap?.get(FirebaseAuth.getInstance().currentUser?.uid)
            if (unread != 0) {
                view.notificationTextView.text = unread.toString()
                view.notificationTextView.visibility = View.VISIBLE
            }
            UserDAO.loadProfileImage(view.context, chatMeta.partner?.uid!!, view.profileImageView)
        }

        private fun getDateStatus(date: Date) : Int {
            val dateFormat = SimpleDateFormat("ddMMyyyy")
            val dateFormatYear = SimpleDateFormat("yyyy")
            val now = Date()
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            val yesterday = cal.time
            return when {
                dateFormat.format(now) == dateFormat.format(date) -> 0
                dateFormat.format(yesterday) == dateFormat.format(date) -> 1
                dateFormatYear.format(now) == dateFormatYear.format(date) -> 2
                else -> 3
            }

        }

    }

}



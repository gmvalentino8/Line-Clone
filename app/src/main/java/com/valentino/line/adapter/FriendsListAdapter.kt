package com.valentino.line.adapter

/**
 * Created by Valentino on 4/2/18.
 */

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.valentino.line.R
import com.valentino.line.dao.UserDAO
import com.valentino.line.model.User

import kotlinx.android.synthetic.main.item_friend.view.*


class FriendsListAdapter(private val friendsDataSet: List<User>) : Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return  friendsDataSet.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendListViewHolder(root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = friendsDataSet[position]
        val placeHolder = holder as FriendListViewHolder
        placeHolder.bindData(data)
    }

    class FriendListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: View = itemView

        fun bindData(user: User) {
            view.nameTextView.setText(user.name)
            if (user.status != null) {
                view.statusTextView.setText(user.status)
            }
            else {
                view.statusTextView.visibility = View.GONE
            }
            UserDAO.loadProfileImage(view.context, user.uid!!, view.profileImageView)
        }
    }
}



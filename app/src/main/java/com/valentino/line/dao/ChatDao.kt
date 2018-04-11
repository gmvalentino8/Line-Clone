package com.valentino.line.dao

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.valentino.line.model.Chat
import com.valentino.line.model.ChatMetadata

/**
 * Created by Valentino on 4/2/18.
 */

object ChatDAO {
    private val mDatabase = FirebaseDatabase.getInstance().reference
    val currentUser = FirebaseAuth.getInstance().currentUser

    fun postChat(chat: Chat) {
        val cidRef = mDatabase.child("chats").push()
        val cid = cidRef.key
        cidRef.setValue(chat)
        for (uid in chat.uidList) {
            mDatabase.child("user-chats").child(uid).child(cid).setValue(1)
        }
    }

    fun getChat(cid: String, completion: (Chat?)->Unit) {
        mDatabase.child("chats").child(cid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                var chat = p0?.getValue(Chat::class.java)
                chat?.cid = p0?.key
                completion(chat)
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    fun getChatsFromUser(uid: String, completion: (Chat?)->Unit) {
        mDatabase.child("user-chats").child(uid).addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                getChat(p0?.key!!, completion)
            }
        })
    }

    fun getChatWithUser(uid: String, completion: (Chat?) -> Unit) {
        var total: Long
        mDatabase.child("user-chats").child(currentUser?.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                Log.d("Get Chats", "My Chats: " + p0?.value)
                total = p0?.childrenCount!!
                val myChatsKeyArray = HashSet<String>()
                for (child in p0.children) {
                    myChatsKeyArray.add(child.key)
                }
                mDatabase.child("user-chats").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot?) {
                        Log.d("Get Chats", "Partner Chats: " + p0?.value)
                        val partnerTotal = p0?.childrenCount!!
                        if (total == 0L || partnerTotal == 0L) {
                            postChat(Chat(uidList = arrayListOf(currentUser?.uid!!, uid)))
                        }

                        for (item in p0.children) {
                            if (item.key in myChatsKeyArray) {
                                getChat(item.key) {chat ->
                                    if (uid in chat?.uidList!! && currentUser?.uid in chat.uidList ) {
                                        completion(chat)
                                    }
                                }
                            }
                        }
                    }
                    override fun onCancelled(p0: DatabaseError?) {}
                })
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    fun getChatMetadata(chat: Chat, completion: (ChatMetadata)->Unit) {
        val chatMeta = ChatMetadata(chat)
        val partnerId = chat.getChatPartner(FirebaseAuth.getInstance().currentUser?.uid!!)
        UserDAO.getUser(partnerId) {
            chatMeta.partner = it
            if (chat.recent == "") {
                completion(chatMeta)
            }
            else {
                MessageDAO.getMessage(chat.recent) {
                    chatMeta.message = it
                    completion(chatMeta)
                }
            }
        }
    }
}
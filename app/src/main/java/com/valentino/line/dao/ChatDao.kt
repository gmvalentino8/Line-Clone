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

    fun postChat(chat: Chat) {
        val cidRef = mDatabase.child("chats").push()
        val cid = cidRef.key
        cidRef.setValue(chat)
        for (item in chat.userMap) {
            mDatabase.child("user-chats").child(item.key).child(cid).setValue(1)
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

    fun getUserChats(completion: (Chat?) -> Unit, updated: (Chat?) -> Unit) {
        mDatabase.child("user-chats").child(FirebaseAuth.getInstance().currentUser?.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                val myChatsKeyArray = HashSet<String>()
                for (child in p0?.children!!) {
                    myChatsKeyArray.add(child.key)
                }
                mDatabase.child("chats").addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                        var chat = p0?.getValue(Chat::class.java)
                        chat?.cid = p0?.key
                        updated(chat)
                    }
                    override fun onChildRemoved(p0: DataSnapshot?) {}
                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0?.key in myChatsKeyArray) {
                            val chat = p0?.getValue(Chat::class.java)
                            chat?.cid = p0?.key
                            completion(chat)
                        }
                    }

                })
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    fun getChatWithUser(uid: String, completion: (Chat?) -> Unit) {
        var total: Long
        mDatabase.child("user-chats").child(FirebaseAuth.getInstance().currentUser?.uid).addListenerForSingleValueEvent(object : ValueEventListener {
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
                            postChat(Chat(userMap = hashMapOf(
                                    FirebaseAuth.getInstance().currentUser?.uid!! to 0,
                                    uid to 0)))
                        }

                        for (item in p0.children) {
                            if (item.key in myChatsKeyArray) {
                                getChat(item.key) {chat ->
                                    if (uid in chat?.userMap?.keys!! && FirebaseAuth.getInstance().currentUser?.uid in chat.userMap.keys ) {
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
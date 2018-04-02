package com.valentino.line.dao

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
    private val mStorage = FirebaseStorage.getInstance().reference
    val currentUser = FirebaseAuth.getInstance().currentUser

    fun postChat(chat: Chat, uidList: List<String>, completion: (String)->Unit) {
        val cidRef = mDatabase.child("chats").push()
        val cid = cidRef.key
        cidRef.setValue(chat)
        for (uid in uidList) {
            mDatabase.child("user-chats").child(uid).setValue(cid)
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

    fun getChatWithUser(uid: String, completion: (Chat?) -> Unit, finish: ()->Unit) {
        mDatabase.child("users-chats").child(currentUser?.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                finish()
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
        mDatabase.child("user-chats").child(currentUser?.uid).addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                mDatabase.child("user-chats").child(uid).equalTo(p0?.key).addChildEventListener(object : ChildEventListener{
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
                    override fun onChildRemoved(p0: DataSnapshot?) {}
                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        var chat = p0?.getValue(Chat::class.java)
                        chat?.cid = p0?.key
                        completion(chat)
                    }
                })
            }
        })
    }

    fun getChatMetadata(chat: Chat, completion: (ChatMetadata)->Unit) {
        var chatMeta = ChatMetadata(chat)
        val partnerId = chat.getChatPartner(FirebaseAuth.getInstance().currentUser?.uid!!)
        UserDAO.getUser(partnerId) {
            chatMeta.partner = it
            MessageDAO.getMessage(chat.recent) {
                chatMeta.message = it
                completion(chatMeta)
            }
        }
    }
}
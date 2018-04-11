package com.valentino.line.dao

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.valentino.line.model.Chat
import com.valentino.line.model.Message
import com.valentino.line.model.User

/**
 * Created by Valentino on 4/2/18.
 */

object MessageDAO {
    private val mDatabase = FirebaseDatabase.getInstance().reference
    private val mStorage = FirebaseStorage.getInstance().reference

    fun postMessage(message: Message, cid: String) {
        val midRef = mDatabase.child("messages").push()
        midRef.setValue(message)
        mDatabase.child("chat-messages").child(cid).child(midRef.key).setValue(1)
        mDatabase.child("chats").child(cid).child("recent").setValue(midRef.key)
    }

    fun getMessage(mid: String, completion: (Message?)->Unit) {
        mDatabase.child("messages").child(mid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                var message = p0?.getValue(Message::class.java)
                message?.mid = p0?.key
                completion(message)
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    fun getMessagesFromChat(cid: String, completion: (Message?)->Unit) {
        mDatabase.child("chat-messages").child(cid).orderByChild("time").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                MessageDAO.getMessage(p0?.key!!, completion)
            }

        })
    }

}
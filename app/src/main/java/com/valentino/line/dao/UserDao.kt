package com.valentino.line.dao

import android.os.Bundle
import com.facebook.*
import com.google.firebase.auth.FirebaseAuth
import com.valentino.line.listener.UserListener
import com.valentino.line.model.User
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import android.widget.Toast
import android.R.attr.author
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.valentino.line.model.Chat
import java.io.ByteArrayOutputStream


/**
 * Created by Valentino on 4/1/18.
 */

object UserDAO {
    private val mDatabase = FirebaseDatabase.getInstance().reference
    private val mStorage = FirebaseStorage.getInstance().reference

    fun getUserDataFromFacebook(completion: (String, String, String, String) -> Unit) {
        val request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            try {
                val firstName = `object`.getString("first_name")
                val lastName = `object`.getString("last_name")
                val picture = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                val email = `object`.getString("email")
                Log.d("Facebook API", firstName + " " + lastName + " " + picture)
                completion(firstName, lastName, email, picture)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "first_name, last_name, email, picture.type(large).width(960).height(960)")
        request.parameters = parameters
        request.executeAsync()
    }

    fun getUser(uid: String, completion: (User?)->Unit) {
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                var user = p0?.getValue(User::class.java)
                user?.uid = p0?.key
                completion(user)
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    fun getUserFromEmail(email: String, completion: (User?)->Unit, finish: ()-> Unit) {
        mDatabase.child("users").orderByChild("email").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                finish()
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
        mDatabase.child("users").orderByChild("email").equalTo(email).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                var user = p0?.getValue(User::class.java)
                user?.uid = p0?.key
                completion(user)
            }
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
        })
    }

    fun postUser(user: User) {
        mDatabase.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).setValue(user)
        mDatabase.child("user-chats").child(FirebaseAuth.getInstance().currentUser?.uid).setValue(0)
    }

    fun saveProfileImage(imageView: ImageView) {
        val bitmap = (imageView.getDrawable() as BitmapDrawable).getBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        val imageName = FirebaseAuth.getInstance().currentUser?.uid + ".png"
        mStorage.child("profileImages").child(imageName).putBytes(image)
    }

    fun loadProfileImage(context: Context, uid: String, imageView: ImageView) {
        val imageName = uid + ".png"
        mStorage.child("profileImages").child(imageName).downloadUrl.addOnSuccessListener {
            Glide.with(context).load(it).into(imageView)
        }

    }

    fun postFriend(user: User?) {
        mDatabase.child("user-friends").child(FirebaseAuth.getInstance().currentUser?.uid).child(user?.uid).setValue(true)
        ChatDAO.postChat(Chat(userMap = hashMapOf(
                FirebaseAuth.getInstance().currentUser?.uid!! to 0,
                user?.uid!! to 0)))
    }

    fun getFriends(completion: (User?) -> Unit) {
        mDatabase.child("user-friends").child(FirebaseAuth.getInstance().currentUser?.uid).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                getUser(p0?.key!!, completion)
            }
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
        })
    }

    fun getFriend(uid: String, completion: () -> Unit) {
        mDatabase.child("user-friends").child(FirebaseAuth.getInstance().currentUser?.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                val friendsSet = HashSet<String>()
                for (child in p0?.children!!) {
                    friendsSet.add(child.key)
                }
                if (!friendsSet.contains(uid)) {
                    completion()
                }
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

}
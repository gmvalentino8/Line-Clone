package com.valentino.line.dao

import android.os.Bundle
import com.facebook.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


/**
 * Created by Valentino on 4/1/18.
 */

object UserDAO {
    private val mDatabase = FirebaseDatabase.getInstance().reference
    private val mStorage = FirebaseStorage.getInstance().reference
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    fun getUser(completion: (User?)->Unit) {
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                var user = p0?.getValue(User::class.java)
                completion(user)
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    fun postUser(user: User) {
        mDatabase.child("users").child(uid).setValue(user)
    }

    fun saveProfileImage(imageView: ImageView) {
        val bitmap = (imageView.getDrawable() as BitmapDrawable).getBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        val imageName = uid + ".png"
        mStorage.child("profileImages").child(imageName).putBytes(image)
    }

    fun getProfileImage(context: Context, userID: String, imageView: ImageView) {
        val imageName = userID + ".png"
        mStorage.child("profileImages").child(imageName).downloadUrl.addOnSuccessListener {
            Glide.with(context).load(it).into(imageView)
        }

    }

    fun saveProfileImage(imageURL: String) {

    }

    fun saveProfileImage(imageURI: Uri) {

    }

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

    fun setUserData(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
                accessToken
        ) { `object`, response ->
            try {
                val firstName = `object`.getString("first_name")
                val lastName = `object`.getString("last_name")
                val email = `object`.getString("email")
                val picture = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                val facebookID = `object`.getString("id")
                val userUpdate = HashMap<String, Any>()
                userUpdate.put("firstName", firstName)
                userUpdate.put("lastName", lastName)
                userUpdate.put("email", email)
                mDatabase.child("users").child(uid).updateChildren(userUpdate)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, last_name, email, picture.type(large).width(960).height(960)")
        request.parameters = parameters
        request.executeAsync()
    }

    fun getFriends(listener:UserListener) {
        val eventsQuery = mDatabase.child("android_users").child(Profile.getCurrentProfile().id).child("friends")
        eventsQuery.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val idList = HashSet<String>()
                for (eventSnapshot in dataSnapshot.children)
                {
                    val userID = eventSnapshot.value!!.toString()
                    idList.add(userID)
                }
                getFriendsFromIDList(idList, listener)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun getFriendsFromIDList(idList:Set<String>, listener:UserListener) {
        val eventsQuery = mDatabase.child("android_users")
        eventsQuery.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val friendsList = ArrayList<User>()
                for (eventSnapshot in dataSnapshot.children)
                {
                    if (idList.contains(eventSnapshot.key))
                    {
                        val friend = eventSnapshot.getValue<User>(User::class.java)
                        friend!!.uid = eventSnapshot.key
                        friendsList.add(friend!!)
                    }
                }
                listener.onSuccess()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun getUserProfile(userID:String, listener: UserListener) {
        val eventsQuery = mDatabase.child("android_users").child(userID)
        eventsQuery.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                val user = dataSnapshot.getValue<User>(User::class.java)
                user!!.uid = dataSnapshot.key
                userList.add(user)
                listener.onSuccess()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


}
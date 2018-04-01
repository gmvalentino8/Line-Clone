package com.valentino.line.model

/**
 * Created by Valentino on 4/1/18.
 */

data class User(var uid: String, var name: String, var email: String) {
    fun copy(user : User) {
        this.uid = user.uid
        this.name = user.name
        this.email = user.email
    }
}
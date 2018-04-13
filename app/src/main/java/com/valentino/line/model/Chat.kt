package com.valentino.line.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Valentino on 4/2/18.
 */
@Parcelize
data class Chat(var cid: String? = null, var recent: String = "", var userMap: Map<String, Int> = HashMap()) : Parcelable {
    fun getChatPartner(currUser: String) : String{
        for (item in userMap) {
            if (item.key != currUser) {
                return item.key
            }
        }
        return ""
    }
}
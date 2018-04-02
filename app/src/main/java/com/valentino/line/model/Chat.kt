package com.valentino.line.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Valentino on 4/2/18.
 */
@Parcelize
data class Chat(var cid: String? = null, var recent: String = "", var uidList: List<String> = ArrayList()) : Parcelable {
    fun getChatPartner(currUser: String) : String{
        for (uid in uidList) {
            if (uid != currUser) {
                return uid
            }
        }
        return ""
    }
}
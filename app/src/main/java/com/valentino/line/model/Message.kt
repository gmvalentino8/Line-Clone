package com.valentino.line.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Valentino on 4/2/18.
 */
@Parcelize
data class Message(var mid: String? = null, var from: String = "", var content: String = "", var time: Long = 0) : Parcelable
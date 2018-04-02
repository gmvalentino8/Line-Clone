package com.valentino.line.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Valentino on 4/1/18.
 */

@Parcelize
data class User(var uid: String? = null, var email: String = "", var name: String = "", var status: String? = null) : Parcelable
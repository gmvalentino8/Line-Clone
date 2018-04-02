package com.valentino.line.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Created by Valentino on 4/2/18.
 */
@Parcelize
data class ChatMetadata(var chat: Chat? = null, var message: Message? = null, var partner: User? = null) : Parcelable
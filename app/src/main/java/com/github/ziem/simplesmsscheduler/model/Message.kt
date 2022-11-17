package com.github.ziem.simplesmsscheduler.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.OffsetDateTime
import java.util.*

@Parcelize
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val dateTime: OffsetDateTime = OffsetDateTime.now().plusMinutes(15),
    val phoneNumber: String = "",
    val contactName: String = "",
    val contactThumbnail: String? = null,
    val message: String = "",
    val state: State = State.Unknown,
    val sendAutomatically: Boolean = false
) : Parcelable
package com.github.ziem.simplesmsscheduler.extension

import com.github.ziem.simplesmsscheduler.database.DatabaseMessage
import com.github.ziem.simplesmsscheduler.model.Message

fun DatabaseMessage.toMessage(): Message {
    return Message(
        id,
        dateTime,
        phoneNumber,
        contactName,
        contactThumbnail,
        message,
        state,
        sendAutomatically
    )
}

fun Message.toDatabaseMessage(): DatabaseMessage {
    return DatabaseMessage(
        id,
        dateTime,
        phoneNumber,
        contactName,
        contactThumbnail,
        message,
        state,
        sendAutomatically
    )
}
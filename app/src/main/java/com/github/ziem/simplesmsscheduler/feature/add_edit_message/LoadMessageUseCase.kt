package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import com.github.ziem.simplesmsscheduler.database.MessagesDao
import com.github.ziem.simplesmsscheduler.extension.toMessage
import com.github.ziem.simplesmsscheduler.model.Message
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadMessageUseCase @Inject constructor(private val messagesDao: MessagesDao) {
    fun execute(messageId: String): Single<Message> {
        return messagesDao.get(messageId)
            .map { it.toMessage() }
    }
}
package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import com.github.ziem.simplesmsscheduler.database.MessagesDao
import com.github.ziem.simplesmsscheduler.extension.toDatabaseMessage
import com.github.ziem.simplesmsscheduler.model.Message
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveMessageUseCase @Inject constructor(private val messagesDao: MessagesDao) {
    fun execute(message: Message): Completable {
        return messagesDao.insert(message.toDatabaseMessage())
    }
}
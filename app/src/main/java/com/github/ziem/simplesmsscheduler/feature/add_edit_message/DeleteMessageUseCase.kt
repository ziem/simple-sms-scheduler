package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import com.github.ziem.simplesmsscheduler.database.MessagesDao
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteMessageUseCase @Inject constructor(private val messagesDao: MessagesDao) {
    fun execute(messageId: String): Completable {
        return messagesDao.delete(messageId)
    }
}

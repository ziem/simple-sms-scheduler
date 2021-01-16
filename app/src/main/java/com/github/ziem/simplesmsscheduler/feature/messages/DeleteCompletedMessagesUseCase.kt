package com.github.ziem.simplesmsscheduler.feature.messages

import com.github.ziem.simplesmsscheduler.database.MessagesDao
import com.github.ziem.simplesmsscheduler.model.State
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteCompletedMessagesUseCase @Inject constructor(private val messagesDao: MessagesDao) {
    fun execute(): Completable {
        return messagesDao.deleteAll(State.Sent)
    }
}
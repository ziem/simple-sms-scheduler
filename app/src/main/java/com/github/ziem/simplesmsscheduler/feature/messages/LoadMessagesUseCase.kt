package com.github.ziem.simplesmsscheduler.feature.messages

import com.github.ziem.simplesmsscheduler.database.MessagesDao
import com.github.ziem.simplesmsscheduler.extension.toMessage
import com.github.ziem.simplesmsscheduler.model.Message
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadMessagesUseCase @Inject constructor(private val messagesDao: MessagesDao) {
    fun execute(): Observable<List<Message>> {
        return messagesDao.getAllOrdered()
            .map { it.map { it.toMessage() } }
            .subscribeOn(Schedulers.io())
    }
}
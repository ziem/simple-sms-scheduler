package com.github.ziem.simplesmsscheduler.feature.messages

import com.github.ziem.simplesmsscheduler.model.Message

sealed class Change {
    object Loading : Change()
    data class Messages(val messages: List<Message>) : Change()
    data class Error(val throwable: Throwable) : Change()
}
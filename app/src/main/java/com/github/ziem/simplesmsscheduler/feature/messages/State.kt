package com.github.ziem.simplesmsscheduler.feature.messages

import com.github.ziem.simplesmsscheduler.model.Message
import com.ww.roxie.BaseState

data class State(
    val messages: List<Message> = listOf(),
    val isLoading: Boolean = true,
    val error: Throwable? = null
) : BaseState
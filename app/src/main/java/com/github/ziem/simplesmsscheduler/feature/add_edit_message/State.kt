package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import com.github.ziem.simplesmsscheduler.model.Message
import com.ww.roxie.BaseState

data class State(
    val message: Message = Message(),
    val isIdle: Boolean = false,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isMessageDeleted: Boolean = false,
    val isMessageSaved: Boolean = false,
    val isValid: Boolean = false
) : BaseState
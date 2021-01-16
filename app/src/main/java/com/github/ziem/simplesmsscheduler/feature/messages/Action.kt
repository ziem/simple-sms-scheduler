package com.github.ziem.simplesmsscheduler.feature.messages

import com.ww.roxie.BaseAction

sealed class Action : BaseAction {
    object LoadMessages : Action()
    object ClearCompletedMessages : Action()
}
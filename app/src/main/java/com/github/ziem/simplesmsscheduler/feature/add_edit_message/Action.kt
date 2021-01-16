package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import com.github.ziem.simplesmsscheduler.model.Contact
import com.github.ziem.simplesmsscheduler.model.Message
import com.ww.roxie.BaseAction
import org.threeten.bp.OffsetDateTime

sealed class Action : BaseAction {
    data class InitMessage(val message: Message) : Action()
    object SaveMessage : Action()
    data class DeleteMessage(val messageId: String) : Action()
    data class ApplyContact(val contact: Contact) : Action()
    data class ApplyMessage(val message: String) : Action()
    data class ApplyDateTime(val dateTime: OffsetDateTime) : Action()
    class SendAutomaticallyRun(val sendAutomatically: Boolean) : Action()
}
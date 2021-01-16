package com.github.ziem.simplesmsscheduler.feature.add_edit_message

import com.github.ziem.simplesmsscheduler.model.Contact
import com.github.ziem.simplesmsscheduler.model.Message
import org.threeten.bp.OffsetDateTime

sealed class Change {
    object Loading : Change()
    data class Init(val message: Message) : Change()
    data class ContactChange(val contact: Contact) : Change()
    data class MessageChange(val message: String) : Change()
    data class DateTimeChange(val dateTime: OffsetDateTime) : Change()
    object MessageSaved : Change()
    data class MessageError(val throwable: Throwable) : Change()
    class SendAutomaticallyChange(val sendAutomatically: Boolean) : Change()
    object MessageDeleted : Change()
}
package com.github.ziem.simplesmsscheduler.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.github.ziem.simplesmsscheduler.database.MessagesDao
import com.github.ziem.simplesmsscheduler.extension.toMessage
import com.github.ziem.simplesmsscheduler.feature.add_edit_message.SaveMessageUseCase
import com.github.ziem.simplesmsscheduler.model.State
import com.github.ziem.simplesmsscheduler.sms.SmsSender
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import toothpick.Toothpick
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_SEND = "action_send"
    }

    @Inject
    lateinit var messagesDao: MessagesDao

    @Inject
    lateinit var smsSender: SmsSender

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var saveMessageUseCase: SaveMessageUseCase

    // TODO: refactor
    override fun onReceive(context: Context, intent: Intent) {
        Toothpick.inject(this, Toothpick.openScope(context.applicationContext))
        Toast.makeText(context, intent.action ?: "no action", Toast.LENGTH_SHORT).show()

        when (intent.action) {
            "android.intent.action.BOOT_COMPLETED" -> {
                messagesDao.getFuture()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .flatMapCompletable {
                        alarmScheduler.scheduleAlarm(it.id, it.dateTime)
                            .andThen(saveMessageUseCase.execute(it.toMessage().copy(state = State.Scheduled)))
                    }
            }
            ACTION_SEND -> {
                Timber.d("Alarm was triggered!")
                val messageId = intent.getStringExtra("message_id") as String

                messagesDao.get(messageId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        Single.fromCallable {
                            smsSender.sendSms(context, it.phoneNumber, it.message, it.sendAutomatically)
                            it
                        }
                    }
                    .map { it.copy(state = State.Sent) }
                    .flatMapCompletable {
                        messagesDao.update(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                    }
                    .subscribe({
                        Toast.makeText(context, "Message send $messageId", Toast.LENGTH_SHORT)
                            .show()
                    }, {
                      Timber.e(it)
                    })
                Timber.d("Message send $messageId")
            }
        }
    }
}


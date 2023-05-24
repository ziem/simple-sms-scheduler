package com.github.ziem.simplesmsscheduler.alarm

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import io.reactivex.Completable
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(private val context: Application) {
    fun scheduleAlarm(messageId: String, dateTime: OffsetDateTime): Completable {
        return Completable.fromCallable {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("message_id", messageId)
                action = AlarmReceiver.ACTION_SEND
            }
            val alarmPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    messageId.hashCode(),
                    alarmIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                dateTime.toInstant().toEpochMilli(),
                alarmPendingIntent
            )
        }
    }

    fun removeAlarm(messageId: String): Completable {
        return Completable.fromCallable {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("message_id", messageId)
                action = AlarmReceiver.ACTION_SEND
            }
            val alarmPendingIntent =
                PendingIntent.getBroadcast(context, messageId.hashCode(), alarmIntent, PendingIntent.FLAG_IMMUTABLE)

            alarmManager.cancel(alarmPendingIntent)
        }
    }
}
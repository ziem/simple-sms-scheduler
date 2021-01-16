package com.github.ziem.simplesmsscheduler.sms

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SmsSender @Inject constructor() {
    @SuppressLint("CheckResult")
    fun sendSms(context: Context, phoneNumber: String, message: String, sendAutomatically: Boolean) {
        if (sendAutomatically) {
            val smsManager = SmsManager.getDefault()
            // check SEND_SMS permission - may be revoked at this point
            val parts = smsManager.divideMessage(message)

            Completable.fromCallable {
                smsManager.sendMultipartTextMessage(
                    phoneNumber,
                    null,
                    parts,
                    null,
                    null
                )
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.i("SMS sent")
                }, {
                    Timber.e(it)
                })
        } else {
            val uri = Uri.parse("smsto:$phoneNumber")
            val sendSmsIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(sendSmsIntent)
        }
    }
}
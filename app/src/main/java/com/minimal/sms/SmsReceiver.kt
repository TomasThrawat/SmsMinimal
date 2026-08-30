package com.minimal.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val parsed = messages.map {
            SmsMessage(
                sender = it.originatingAddress ?: "Unknown",
                body = it.messageBody ?: "",
                timestamp = it.timestampMillis
            )
        }
        SmsStore.addAll(parsed)
    }
}

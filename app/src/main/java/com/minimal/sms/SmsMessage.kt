package com.minimal.sms

data class SmsMessage(
    val sender: String,
    val body: String,
    val timestamp: Long
)

package com.minimal.sms

object SmsStore {

    private val messages = mutableListOf<SmsMessage>()
    private var listener: (() -> Unit)? = null

    fun setListener(l: (() -> Unit)?) {
        listener = l
    }

    fun getAll(): List<SmsMessage> = messages.sortedByDescending { it.timestamp }

    fun addAll(newOnes: List<SmsMessage>) {
        for (m in newOnes) {
            if (messages.none { it.sender == m.sender && it.body == m.body && it.timestamp == m.timestamp }) {
                messages.add(m)
            }
        }
        listener?.invoke()
    }

    fun add(message: SmsMessage) {
        messages.add(message)
        listener?.invoke()
    }
}

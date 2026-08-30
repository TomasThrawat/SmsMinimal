package com.minimal.sms

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: SmsAdapter

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result[Manifest.permission.RECEIVE_SMS] == true) {
                loadExistingInbox()
            } else {
                Toast.makeText(this, getString(R.string.grant_permission), Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        emptyText = findViewById(R.id.emptyText)

        adapter = SmsAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<TextView>(R.id.clearButton).setOnClickListener {
            SmsStore.clear()
        }

        SmsStore.setListener { refreshList() }

        ensurePermissions()
        refreshList()
    }

    override fun onDestroy() {
        super.onDestroy()
        SmsStore.setListener(null)
    }

    private fun ensurePermissions() {
        val needed = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) needed.add(Manifest.permission.RECEIVE_SMS)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) needed.add(Manifest.permission.READ_SMS)

        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        } else {
            loadExistingInbox()
        }
    }

    private fun loadExistingInbox() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val existing = mutableListOf<SmsMessage>()
        val uri: Uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )
        contentResolver.query(uri, projection, null, null, "${Telephony.Sms.DATE} DESC")
            ?.use { cursor ->
                val addressIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
                while (cursor.moveToNext()) {
                    existing.add(
                        SmsMessage(
                            sender = cursor.getString(addressIdx) ?: "Unknown",
                            body = cursor.getString(bodyIdx) ?: "",
                            timestamp = cursor.getLong(dateIdx)
                        )
                    )
                }
            }
        SmsStore.addAll(existing)
    }

    private fun refreshList() {
        val data = SmsStore.getAll()
        adapter.update(data)
        emptyText.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
    }
}

package com.example.mservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val actionMusic = intent.getIntExtra("action_music", 0)

        val intent_ser = Intent(context,MyService::class.java)
        intent_ser.putExtra("action_mucsic_backser",actionMusic)

        context.startService(intent_ser)
    }
}
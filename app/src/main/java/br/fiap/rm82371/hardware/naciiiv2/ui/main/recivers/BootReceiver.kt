package br.fiap.rm82371.hardware.naciiiv2.ui.main.recivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TESTERECEIVE","Sistema Reiniciado!")
    }
}
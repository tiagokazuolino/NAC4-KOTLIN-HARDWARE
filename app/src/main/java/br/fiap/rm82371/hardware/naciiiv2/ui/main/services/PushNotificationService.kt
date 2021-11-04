package br.fiap.rm82371.hardware.naciiiv2.ui.main.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {
    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

    }
}
package uk.co.explose.schminder.android.core

import android.content.Context
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.explose.schminder.android.model.mpp.MedsRepo

class WearMessageListener(private val context: Context) : MessageClient.OnMessageReceivedListener {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/medication/markTaken") {
            val payload = String(messageEvent.data, Charsets.UTF_8)
            val parts = payload.split("|")
            if (parts.size == 3 && parts[0] == "markTaken") {
                val medId = parts[1].toIntOrNull()
                val timestamp = parts[2].toLongOrNull()

                if (medId != null && timestamp != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        MedsRepo(context).markMedicationAsTaken(medId)
                        //markMedicationAsTaken(medId, timestamp)
                    }
                }
            }
        }
    }

}


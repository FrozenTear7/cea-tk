package actors

import kotlinx.coroutines.channels.Channel
import messages.IMessage

class Logger(private val channel: Channel<IMessage>) {
    suspend fun logActors() {
        while(true) {
            println("Received ping from ${channel.receive().messageValue}")
        }
    }
}
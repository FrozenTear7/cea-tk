package actors

import kotlinx.coroutines.channels.Channel
import messages.IMessage
import messages.MessagePing

class Logger(private val channel: Channel<IMessage>) {
    suspend fun logActors() {
        while(true) {
            val messagePing: MessagePing = channel.receive() as MessagePing
            println("Received ping from ${messagePing.actor.id}")
        }
    }
}
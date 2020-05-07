package actors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import messages.IMessage
import messages.MessagePing
import messages.MessageType

class Actor(private val actorId: Int, private val logChannel: Channel<IMessage>) {
    val actorChannel = Channel<IMessage>()

    private suspend fun channelListen() {
        while (true) {
            val msg = actorChannel.receive()

            when (msg.messageType) {
                MessageType.PING -> println("Received ping from ${msg.messageValue}")
                MessageType.REPRODUCE -> println("Received reproduce from ${msg.messageValue}")
            }

        }
    }

    suspend fun doActorStuff() {
        // Launch a coroutine for consuming incoming messages
        GlobalScope.launch {
            channelListen()
        }

        while (true) {
            logChannel.send(MessagePing(MessageType.PING, actorId))
            delay(2000L)
        }
    }
}
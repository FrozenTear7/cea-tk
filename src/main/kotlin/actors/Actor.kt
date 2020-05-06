package actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import messages.IMessage
import messages.MessagePing
import messages.MessageType

class Actor(private val actorId: Int, private val channel: Channel<IMessage>) {
    suspend fun doActorStuff() {
        while(true) {
            channel.send(MessagePing(MessageType.PING, actorId))
            delay(2000L)
        }
    }
}
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
    private var neighbours: MutableList<Actor> = ArrayList()
    var currentValue = 0

    fun addNeighbour(neighbour: Actor) {
        neighbours.add(neighbour)
    }

    private suspend fun channelListen() {
        while (true) {
            val msg = actorChannel.receive()

            when (msg.messageType) {
                MessageType.PING -> {
                    println("$actorId received ping with value ${msg.messageValue}")
                    currentValue = msg.messageValue + 1
                    delay(2000L)
                    for (neighbour in neighbours) {
                        neighbour.actorChannel.send(MessagePing(MessageType.PING, currentValue))
                    }
                }
                MessageType.REPRODUCE -> println("$actorId received reproduce from ${msg.messageValue}")
            }
        }
    }

    suspend fun doActorStuff() {
        // Launch a coroutine for consuming incoming messages
        GlobalScope.launch {
            channelListen()
        }

        if (actorId == 1) {
            for (neighbour in neighbours) {
                neighbour.actorChannel.send(MessagePing(MessageType.PING, currentValue))
            }
        }
    }
}
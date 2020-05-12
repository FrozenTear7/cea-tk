package actors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import messages.*

class Actor(val id: Int, private val logChannel: Channel<IMessage>) {
    val actorChannel = Channel<IMessage>()
    private var neighbours: MutableList<Actor> = ArrayList()
    private var genotype: IGenotype = GenotypeExample1()
    private var bestGenotype: IGenotype = genotype

    fun addNeighbour(neighbour: Actor) {
        neighbours.add(neighbour)
    }

    private suspend fun channelListen() {
        while (true) {
            val msg = actorChannel.receive()

            when (msg.messageType) {
                MessageType.PING -> {
                    val messagePing: MessagePing = msg as MessagePing
                    println("$id received ping from actor ${messagePing.actor.id}")
                    delay(2000L)

                    messagePing.actor.actorChannel.send(MessagePong(MessageType.PONG, this))

                    for (neighbour in neighbours) {
                        neighbour.actorChannel.send(MessagePing(MessageType.PING, this))
                    }
                }
                MessageType.PONG -> {
                    val messagePong: MessagePong = msg as MessagePong
                    println("$id received pong from actor ${messagePong.actor.id}")

                    // Check if received genotype is better than actor's current and overwrite if it is
//                    messagePong.actor.bestGenotype
                }
                MessageType.REPLACE -> {
                    val messageReplace: MessageReplace = msg as MessageReplace
                    println("$id received replace")
                    genotype = messageReplace.genotype
                }
                MessageType.LOGGER_PING -> {
                    println("$id replying to logger")
                    logChannel.send(MessageLoggerPong(MessageType.LOGGER_PONG, bestGenotype))
                }
                else -> {
                    println("$id received wrong type of request")
                }
            }
        }
    }

    suspend fun doActorStuff() {
        // Launch a coroutine for consuming incoming messages
        GlobalScope.launch {
            channelListen()
        }

        if (id == 1) {
            for (neighbour in neighbours) {
                neighbour.actorChannel.send(MessagePing(MessageType.PING, this))
            }
        }
    }
}
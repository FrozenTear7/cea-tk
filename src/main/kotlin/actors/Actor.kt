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
    private var bestGenotype: BestGenotype = BestGenotype(genotype)

    fun addNeighbour(neighbour: Actor) {
        neighbours.add(neighbour)
    }

    private suspend fun channelListen() {
        while (true) {
            when (val msg = actorChannel.receive()) {
                is MessagePing -> {
                    println("$id received ping from actor ${msg.actor.id}")
                    delay(2000L)

                    msg.actor.actorChannel.send(MessagePong(this))

                    for (neighbour in neighbours) {
                        neighbour.actorChannel.send(MessagePing(this))
                    }

//                     Check if received genotype is better than actor's current and overwrite if it is
//                    bestGenotype.genotype = msg.actor.bestGenotype.genotype
                }
                is MessagePong -> {
                    println("$id received pong from actor ${msg.actor.id}")

//                     Check if received genotype is better than actor's current and overwrite if it is
//                    bestGenotype.genotype = msg.actor.bestGenotype.genotype
                }
                is MessageReplace -> {
                    println("$id received replace")
                    genotype = msg.genotype
                }
                is MessageLoggerPing -> {
                    println("$id replying to logger")
                    logChannel.send(MessageLoggerPong(bestGenotype))
                }
                else -> {
                    println("$id received wrong type of request")
                }
            }
        }
    }

    suspend fun doActorStuff() {
        println("$id time genotype ${bestGenotype.getFormattedTimestamp()}")

        // Launch a coroutine for consuming incoming messages
        GlobalScope.launch {
            channelListen()
        }

        if (id == 1) {
            for (neighbour in neighbours) {
                neighbour.actorChannel.send(MessagePing(this))
            }
        }
    }
}
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

    fun setNeighbours(neighbours: MutableList<Actor>) {
        this.neighbours = neighbours
    }

    private suspend fun channelListen() {
        while (true) {
            when (val msg = actorChannel.receive()) {
                is MessagePing -> {
                    println("$id received ping from actor ${msg.actor.id}")

                    delay(50L)

                    msg.actor.actorChannel.send(MessagePong(this))

//                     Check if received genotype is better than actor's current and overwrite if it is
//                    bestGenotype.genotype = msg.actor.bestGenotype.genotype
                }
                is MessagePong -> {
                    println("$id received pong from actor ${msg.actor.id}")

                    // simplified reproduce, only for testing
                    // the new child replaces whoever is worse than him, otherwise is discarded
                    val newGenotype = genotype.reproduce(msg.actor.genotype)
                    if (msg.actor.genotype.fitness() < newGenotype.fitness()) {
                        println("Partner is worse, sending him replace: ${msg.actor.genotype} -> $genotype")
                        msg.actor.actorChannel.send(MessageReplace(newGenotype))
                    } else if (genotype.fitness() < newGenotype.fitness()) {
                        println("I'm worse, replacing myself: $genotype -> $newGenotype")
                        genotype = newGenotype
                    } else {
                        println("Child is disappointing, abandoning: $newGenotype")
                    }

//                     Check if received genotype is better than actor's current and overwrite if it is
//                    bestGenotype.genotype = msg.actor.bestGenotype.genotype
                }
                is MessageReplace -> {
                    println("$id received replace: $genotype -> ${msg.genotype}")
                    genotype = msg.genotype
                    bestGenotype.genotype = genotype // only for testing
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
            while (true) {
                for (neighbour in neighbours) {
                    neighbour.actorChannel.send(MessagePing(this))
                    delay(50L)
                }
                delay(3000L)
            }
        }
    }
}
package actors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import messages.*
import utils.Printer

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
                    Printer.msg("$id received ping from actor ${msg.actor.id}")

                    delay(50L)

                    msg.actor.actorChannel.send(MessagePong(this))

                    if (bestGenotype.genotype.fitness() < msg.actor.bestGenotype.genotype.fitness()) {
                        bestGenotype.genotype = msg.actor.bestGenotype.genotype
                    } else if (bestGenotype.genotype.fitness() > msg.actor.bestGenotype.genotype.fitness()) {
                        msg.actor.bestGenotype.genotype = bestGenotype.genotype
                    }
                }
                is MessagePong -> {
                    Printer.msg("$id received pong from actor ${msg.actor.id}")

                    // simplified reproduce, only for testing
                    // the new child replaces whoever is worse than him, otherwise is discarded
                    val newGenotype = genotype.reproduce(msg.actor.genotype)

                    when {
                        msg.actor.genotype.fitness() < newGenotype.fitness() -> {
                            Printer.msg("Partner is worse, sending him replace: ${msg.actor.genotype} -> $genotype")
                            msg.actor.actorChannel.send(MessageReplace(newGenotype))
                        }
                        genotype.fitness() < newGenotype.fitness() -> {
                            Printer.msg("I'm worse, replacing myself: $genotype -> $newGenotype")
                            genotype = newGenotype
                        }
                        else -> {
                            Printer.msg("Child is disappointing, abandoning: $newGenotype")
                        }
                    }

                    if (bestGenotype.genotype.fitness() < msg.actor.bestGenotype.genotype.fitness()) {
                        bestGenotype.genotype = msg.actor.bestGenotype.genotype
                    } else if (bestGenotype.genotype.fitness() > msg.actor.bestGenotype.genotype.fitness()) {
                        msg.actor.bestGenotype.genotype = bestGenotype.genotype
                    }
                }
                is MessageReplace -> {
                    Printer.msg("$id received replace: $genotype -> ${msg.genotype}")
                    genotype = msg.genotype
                    bestGenotype.genotype = genotype // only for testing
                }
                is MessageLoggerPing -> {
                    Printer.msg("$id replying to logger")
                    logChannel.send(MessageLoggerPong(bestGenotype))
                }
                else -> {
                    Printer.msg("$id received wrong type of request")
                }
            }
        }
    }

    suspend fun doActorStuff() {
        Printer.msg("$id time genotype ${bestGenotype.getFormattedTimestamp()}")

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
                delay(1000L)
            }
        }
    }
}
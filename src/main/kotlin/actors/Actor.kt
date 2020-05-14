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
                    Printer.msg("$id received ping from actor ${msg.actorId}")

                    delay(50L)

                    msg.responseChannel.send(MessagePong(id, genotype, bestGenotype, actorChannel))

                    if (bestGenotype.genotype.fitness() < msg.bestGenotype.genotype.fitness()) {
                        bestGenotype.genotype = msg.bestGenotype.genotype
                    }
                }
                is MessagePong -> {
                    Printer.msg("$id received pong from actor ${msg.actorId}")

                    // simplified reproduce, only for testing
                    // the new child replaces whoever is worse than him, otherwise is discarded
                    val newGenotype = genotype.reproduce(msg.genotype)

                    when {
                        msg.genotype.fitness() < newGenotype.fitness() -> {
                            Printer.msg("Partner is worse, sending him replace: ${msg.genotype} -> $genotype")
                            msg.responseChannel.send(MessageReplace(newGenotype))
                        }
                        genotype.fitness() < newGenotype.fitness() -> {
                            Printer.msg("I'm worse, replacing myself: $genotype -> $newGenotype")
                            genotype = newGenotype
                        }
                        else -> {
                            Printer.msg("Child is disappointing, abandoning: $newGenotype")
                        }
                    }

                    if (bestGenotype.genotype.fitness() < msg.bestGenotype.genotype.fitness()) {
                        bestGenotype.genotype = msg.bestGenotype.genotype
                    } else if (bestGenotype.genotype.fitness() < newGenotype.fitness()) {
                        bestGenotype.genotype = newGenotype
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
                    neighbour.actorChannel.send(MessagePing(id, bestGenotype, actorChannel))
                    delay(50L)
                }
                delay(1000L)
            }
        }
    }
}
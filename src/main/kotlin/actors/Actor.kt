package actors

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import messages.*
import utils.Printer
import java.util.*
import kotlin.collections.ArrayList

class Actor(val id: Int, private val logChannel: Channel<IMessage>) {
    val actorChannel = Channel<IMessage>()
    private var neighbours: MutableList<Channel<IMessage>> = ArrayList()
    private var genotype: IGenotype = GenotypeExample1()
    private var bestGenotype: BestGenotype = BestGenotype(genotype)

    private var responseQueue: Queue<Pair<Channel<IMessage>, IMessage>> = LinkedList()

    fun setNeighbours(neighbours: MutableList<Channel<IMessage>>) {
        this.neighbours = neighbours
    }

    private suspend fun channelListen() {
        while (true) {
            when (val msg = actorChannel.receive()) {
                is MessagePing -> {
                    Printer.msg("$id received ping from actor ${msg.actorId}")

                    synchronized(responseQueue) {
                        responseQueue.add(
                            Pair(
                                msg.responseChannel,
                                MessagePong(id, genotype, bestGenotype, actorChannel)
                            )
                        )
                    }

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

                            synchronized(responseQueue) {
                                responseQueue.add(Pair(msg.responseChannel, MessageReplace(newGenotype)))
                            }
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

                    synchronized(responseQueue) {
                        responseQueue.add(Pair(logChannel, MessageLoggerPong(bestGenotype)))
                    }
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

        while (true) {
            while (!responseQueue.isEmpty()) {
                var responsePair: Pair<Channel<IMessage>, IMessage>

                synchronized(responseQueue) {
                    responsePair = responseQueue.remove()
                }

                val responseChannel = responsePair.first
                val responseMsg = responsePair.second

                responseChannel.send(responseMsg)
            }

            for (neighbourChannel in neighbours) {
                Printer.msg("$id pinging")
                neighbourChannel.send(MessagePing(id, bestGenotype, actorChannel))
            }
        }
    }
}
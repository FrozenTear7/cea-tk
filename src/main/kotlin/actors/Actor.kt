package actors

import genotypes.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import messages.*
import utils.Printer
import utils.TimeFormat
import java.util.*
import kotlin.collections.ArrayList

class Actor(val id: Int, private val logChannel: Channel<IMessage>, private val nIter: Int) {
    val actorChannel = Channel<IMessage>()
    private var neighbours: MutableList<Channel<IMessage>> = ArrayList()
    private var genotype: IGenotype = GenotypeExample1()
    private var bestGenotype: BestGenotype =
        BestGenotype(genotype)

    private var defaultResponseQueue: Queue<Pair<Channel<IMessage>, IMessage>> = LinkedList()
    private var receivedPongs: MutableList<Triple<Channel<IMessage>, IGenotype, BestGenotype>> = ArrayList()

    fun setNeighbours(neighbours: MutableList<Channel<IMessage>>) {
        this.neighbours = neighbours
    }

    private suspend fun channelListen() {
        while (true) {
            when (val msg = actorChannel.receive()) {
                is MessagePing -> messagePingHandler(msg)
                is MessagePong -> messagePongHandler(msg)
                is MessageReplace -> messageReplaceHandler(msg)
                is MessageLoggerPing -> messageLoggerPingHandler()
                else -> Printer.msg("$id received wrong type of request")
            }
        }
    }

    private fun messagePingHandler(msg: MessagePing) {
        Printer.msg("$id received ping from actor ${msg.actorId}")

        synchronized(defaultResponseQueue) {
            defaultResponseQueue.add(
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

    private fun messagePongHandler(msg: MessagePong) {
        Printer.msg("$id received pong from actor ${msg.actorId}")

        synchronized(receivedPongs) {
            receivedPongs.add(
                Triple(
                    msg.responseChannel,
                    msg.genotype,
                    msg.bestGenotype
                )
            )
        }
    }

    private fun messageReplaceHandler(msg: MessageReplace) {
        Printer.msg("$id received replace: $genotype -> ${msg.genotype}")
        genotype = msg.genotype
    }

    private fun messageLoggerPingHandler() {
        Printer.msg("$id replying to logger")

        synchronized(defaultResponseQueue) {
            defaultResponseQueue.add(Pair(logChannel, MessageLoggerPong(bestGenotype)))
        }
    }

    suspend fun doActorStuff() {
        Printer.msg("$id time genotype ${TimeFormat.getFormattedTimestamp(bestGenotype.timestamp)}")

        // Launch a coroutine for consuming incoming messages
        GlobalScope.launch {
            channelListen()
        }

        for (i in 0 until nIter) {
            for (neighbourChannel in neighbours) {
                Printer.msg("$id pinging")
                neighbourChannel.send(MessagePing(id, bestGenotype, actorChannel))
            }

            handleDefaultResponseQueue()
            reproduce()
        }
        logChannel.send(MessageFinish())
    }

    private suspend fun handleDefaultResponseQueue() {
        while (!defaultResponseQueue.isEmpty()) {
            var responsePair: Pair<Channel<IMessage>, IMessage>

            synchronized(defaultResponseQueue) {
                responsePair = defaultResponseQueue.remove()
            }

            val responseChannel = responsePair.first
            val responseMsg = responsePair.second

            responseChannel.send(responseMsg)
        }
    }

    private fun reproduce() {
        var responseTriple: Triple<Channel<IMessage>, IGenotype, BestGenotype>

        synchronized(receivedPongs) {
            if (receivedPongs.isEmpty()) {
                return
            }
            responseTriple = receivedPongs[0]
            receivedPongs.clear()
        }

        val chosenChannel = responseTriple.first
        val chosenGenotype = responseTriple.second
        val chosenBestGenotype = responseTriple.third
        val newGenotype = genotype.reproduce(chosenGenotype)

        when {
            chosenGenotype.fitness() < newGenotype.fitness() -> {
                Printer.msg("Partner is worse, sending him replace: $chosenGenotype -> $genotype")

                synchronized(defaultResponseQueue) {
                    defaultResponseQueue.add(Pair(chosenChannel, MessageReplace(newGenotype)))
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

        if (bestGenotype.genotype.fitness() < chosenBestGenotype.genotype.fitness()) {
            bestGenotype.genotype = chosenBestGenotype.genotype
        } else if (bestGenotype.genotype.fitness() < newGenotype.fitness()) {
            bestGenotype.genotype = newGenotype
        }
    }
}
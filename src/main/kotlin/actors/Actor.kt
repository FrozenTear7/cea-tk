package actors

import genotypeChoosers.GenotypeBestChooser
import genotypeChoosers.GenotypeChooser
import genotypeChoosers.GenotypeWorstChooser
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
    private var reproduceChooser: GenotypeChooser = GenotypeBestChooser()
    private var removeChooser: GenotypeChooser = GenotypeWorstChooser()
    private var genotype: IGenotype = GenotypeExample3()
    private var bestGenotype: BestGenotype =
        BestGenotype(genotype)

    private var defaultResponseQueue: Queue<Pair<Channel<IMessage>, IMessage>> = LinkedList()
    private var receivedPongs: Triple<
            MutableList<Channel<IMessage>>,
            MutableList<IGenotype>,
            MutableList<BestGenotype>
            > = Triple(ArrayList(), ArrayList(), ArrayList())

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
            receivedPongs.first.add(msg.responseChannel)
            receivedPongs.second.add(msg.genotype)
            receivedPongs.third.add(msg.bestGenotype)

            if (receivedPongs.second.size == neighbours.size) {
                reproduce()
                receivedPongs = Triple(ArrayList(), ArrayList(), ArrayList())
            }
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
        var removeChannel: Channel<IMessage>
        var chosenGenotype: IGenotype

        val reproduceIndex = reproduceChooser.choose(receivedPongs.second)
        val removeIndex = removeChooser.choose(receivedPongs.second)

        removeChannel = receivedPongs.first[removeIndex]
        chosenGenotype = receivedPongs.second[reproduceIndex]

        val newGenotype = genotype.reproduce(chosenGenotype)

        synchronized(defaultResponseQueue) {
            defaultResponseQueue.add(Pair(removeChannel, MessageReplace(newGenotype)))
        }

        if (bestGenotype.genotype.fitness() < newGenotype.fitness()) {
            bestGenotype.genotype = newGenotype
        }
    }
}
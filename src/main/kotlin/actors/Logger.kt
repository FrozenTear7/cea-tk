package actors

import genotypes.BestGenotype
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import messages.IMessage
import messages.MessageFinish
import messages.MessageLoggerPing
import messages.MessageLoggerPong
import utils.Printer
import utils.TimeFormat

class Logger(private val sentinels: List<Actor>, private val channel: Channel<IMessage>, private val totalActors: Int) {
    private var bestGenotype: BestGenotype? = null
    private var actorsFinished = 0

    private suspend fun receiveLogs() {
        while (actorsFinished < totalActors) {
            when (val msg = channel.receive()) {
                is MessageLoggerPong -> {
                    Printer.msg("Logger received actor's best genotype, ${msg.bestGenotype.genotype}")

                    val prevBestGenotypeFitness: Double = bestGenotype?.genotype?.fitness() ?: 0.0

                    if (bestGenotype == null) {
                        bestGenotype = msg.bestGenotype
                    } else if (msg.bestGenotype.genotype.fitness() > bestGenotype!!.genotype.fitness()) {
                        bestGenotype = msg.bestGenotype
                    }

                    if (bestGenotype!!.genotype.fitness() > prevBestGenotypeFitness) {
                        Printer.msg(
                            "Current best logged genotype: ${bestGenotype!!.genotype}, fitness: ${bestGenotype!!.genotype.fitness()}, updated at: " +
                                    "${TimeFormat.getFormattedTimestamp(bestGenotype!!.timestamp)}",
                            crucial = true
                        )
                    }
                }
                is MessageFinish -> {
                    actorsFinished++
                    Printer.msg("Logger received actor finish $actorsFinished out of $totalActors")
                }
                else -> {
                    Printer.msg("Logger received wrong type of reqeust")
                }
            }
        }
    }

    suspend fun pingActors() {
        // Launch a coroutine for consuming incoming messages
        GlobalScope.launch {
            receiveLogs()
        }

        while (actorsFinished < totalActors) {
            for (sentinel in sentinels) {
                Printer.msg("Logger asking ${sentinel.id} for best genotype")
                sentinel.actorChannel.send(MessageLoggerPing())
            }
        }
    }
}
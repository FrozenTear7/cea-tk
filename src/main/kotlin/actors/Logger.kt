package actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import messages.IMessage
import messages.MessageLoggerPing
import messages.MessageLoggerPong
import utils.Printer

class Logger(private val sentinels: List<Actor>, private val channel: Channel<IMessage>) {
    private var bestGenotype: BestGenotype? = null

    suspend fun logActors() {
        while (true) {
            for (sentinel in sentinels) {
                Printer.msg("Logger asking ${sentinel.id} for best genotype")
                sentinel.actorChannel.send(MessageLoggerPing())

                when (val msg = channel.receive()) {
                    is MessageLoggerPong -> {
                        Printer.msg("Logger received actor's best genotype, ${msg.bestGenotype.genotype}")

                        if (bestGenotype == null) {
                            bestGenotype = msg.bestGenotype
                        }
                        else if (msg.bestGenotype.genotype.fitness() > bestGenotype!!.genotype.fitness()) {
                            bestGenotype = msg.bestGenotype
                        }

                        Printer.msg("Current best logged genotype: ${bestGenotype!!.genotype}", crucial=true)
                    }

                    else -> {
                        Printer.msg("Logger received wrong type of reqeust")
                    }
                }
            }

            delay(5000L)
        }
    }
}
package actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import messages.IMessage
import messages.MessageLoggerPing
import messages.MessageLoggerPong

class Logger(private val sentinels: List<Actor>, private val channel: Channel<IMessage>) {
    private var bestGenotype: BestGenotype? = null

    suspend fun logActors() {
        while (true) {
            for (sentinel in sentinels) {
                println("Logger asking ${sentinel.id} for best genotype")
                sentinel.actorChannel.send(MessageLoggerPing())

                when (val msg = channel.receive()) {
                    is MessageLoggerPong -> {
                        println("Logger received actor's best genotype")

                        // Later on add a check if the genotype is better than Logger's current best
                        bestGenotype = msg.bestGenotype
                    }
                    else -> {
                        println("Logger received wrong type of reqeust")
                    }
                }
            }

            delay(2000L)
        }
    }
}
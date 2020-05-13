package actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import messages.IMessage
import messages.MessageLoggerPing
import messages.MessageLoggerPong
import messages.MessageType

class Logger(private val sentinels: List<Actor>, private val channel: Channel<IMessage>) {
    private var bestGenotype: BestGenotype? = null

    suspend fun logActors() {
        while (true) {
            for (sentinel in sentinels) {
                println("Logger asking ${sentinel.id} for best genotype")
                sentinel.actorChannel.send(MessageLoggerPing(MessageType.LOGGER_PING))

                val msg = channel.receive()

                if (msg.messageType == MessageType.LOGGER_PONG) {
                    println("Logger received actor's best genotype")

                    val messageLoggerPong = msg as MessageLoggerPong
                    // Later on add a check if the genotype is better than Logger's current best
                    bestGenotype = messageLoggerPong.bestGenotype
                } else {
                    println("Logger received wrong type of reqeust")
                }
            }

            delay(2000L)
        }
    }
}
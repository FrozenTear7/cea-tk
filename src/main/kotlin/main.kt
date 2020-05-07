import actors.Actor
import actors.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import messages.IMessage
import messages.MessagePing
import messages.MessageType

suspend fun launchActors(m: Int, n: Int) {
    val logChannel = Channel<IMessage>()

    for (i in 1..m * n) {
        val actor = Actor(i, logChannel)

        GlobalScope.launch {
            actor.doActorStuff()
        }

        actor.actorChannel.send(MessagePing(MessageType.REPRODUCE, 123))
    }

    Logger(logChannel).logActors()
}

fun main(args: Array<String>) = runBlocking {
    val m = args[0].toInt()
    val n = args[1].toInt()

    launchActors(m, n) // Create m*n actors
}
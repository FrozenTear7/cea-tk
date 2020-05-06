import actors.Actor
import actors.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import messages.IMessage

suspend fun launchActors(m: Int, n: Int) {
    coroutineScope {
        val channel = Channel<IMessage>()

        for (i in 1..m * n) {
            launch {
                Actor(i, channel).doActorStuff()
            }
        }

        Logger(channel).logActors()
    }
}

fun main(args: Array<String>) = runBlocking {
    val m = args[0].toInt()
    val n = args[1].toInt()

    launchActors(m, n) // Create m*n actors
}
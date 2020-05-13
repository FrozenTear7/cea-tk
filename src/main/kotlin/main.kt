import actorSpawners.CrossActorSpawner
import actors.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import messages.IMessage
import utils.Printer

suspend fun launchActors(m: Int, n: Int, x: Int) {
    val logChannel = Channel<IMessage>()
    val actorSpawner = CrossActorSpawner(m, n)

    val actorList = actorSpawner.spawnActors(logChannel)

    for (actor in actorList) {
        GlobalScope.launch {
            actor.doActorStuff()
        }
    }

    Logger(actorList.slice(IntRange(0, x - 1)), logChannel).logActors()
}

fun main(args: Array<String>) = runBlocking {
    val m = args[0].toInt()
    val n = args[1].toInt()
    val x = args[2].toInt()  // Number of actor talking to Logger

    Printer.verbose = true

    launchActors(m, n, x) // Create m*n actors
}
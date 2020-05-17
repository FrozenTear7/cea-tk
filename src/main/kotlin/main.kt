import actorSpawners.CrossActorSpawner
import actors.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import messages.IMessage
import utils.Printer
import kotlin.system.exitProcess

suspend fun launchActors(m: Int, n: Int, x: Int, nIter: Int) {
    val logChannel = Channel<IMessage>()
    val actorSpawner = CrossActorSpawner(m, n, nIter)

    val actorList = actorSpawner.spawnActors(logChannel)

    for (actor in actorList) {
        GlobalScope.launch {
            actor.doActorStuff()
        }
    }

    Logger(actorList.slice(IntRange(0, x - 1)), logChannel, m * n).pingActors()
}

fun main(args: Array<String>): Unit = runBlocking {
    if (args.size < 4) {
        println("Please provide valid arguments: <m> <n> <n_sentinels> <n_iterations> - m * n grid")
        exitProcess(-1)
    }

    val m = args[0].toInt()
    val n = args[1].toInt()
    var x = args[2].toInt()  // Number of actors talking to Logger, will default to m*n if greater
    val nIter = args[3].toInt()
    if (x > m * n) x = m * n

    Printer.verbose = false

    val startActorsTime: Long = System.currentTimeMillis()

    launchActors(m, n, x, nIter) // Create m*n actors

    val totalTime: Long = System.currentTimeMillis() - startActorsTime

    println("Process took: ${totalTime / 1000}.${totalTime % 1000}s")

    exitProcess(0)
}
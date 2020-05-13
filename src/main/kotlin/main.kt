import actors.Actor
import actors.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import messages.IMessage

suspend fun launchActors(m: Int, n: Int, x: Int) {
    val logChannel = Channel<IMessage>()
    val actorList: MutableList<Actor> = ArrayList()

    for (i in 1..m * n) {
        val newActor = Actor(i, logChannel)
        actorList.add(newActor)
    }

    for (i in 0 until m * n) {
        val actor = actorList[i]
        if (i == 0) {
            actor.addNeighbour(actorList[m * n - 1])
        } else {
            actor.addNeighbour(actorList[i - 1])
        }
    }

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

    launchActors(m, n, x) // Create m*n actors
}
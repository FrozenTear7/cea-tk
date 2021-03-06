package actorSpawners

import actors.Actor
import kotlinx.coroutines.channels.Channel
import messages.IMessage

abstract class ActorSpawner(
    protected val m: Int,
    protected val n: Int,
    private val nIter: Int
) {
    protected var actorGrid: MutableList<MutableList<Actor>> = ArrayList()

    fun spawnActors(logChannel: Channel<IMessage>): MutableList<Actor> {
        initActors(logChannel)
        attachNeighbours()
        return flattenGrid()
    }

    private fun initActors(logChannel: Channel<IMessage>) {
        for (i in 0 until m) {
            val actorRow: MutableList<Actor> = ArrayList()
            for (j in 0 until n) {
                actorRow.add(Actor(i * n + j, logChannel, nIter))
            }
            actorGrid.add(actorRow)
        }
    }

    protected abstract fun attachNeighbours()

    private fun flattenGrid(): MutableList<Actor> {
        val actors: MutableList<Actor> = ArrayList()
        for (actorRow in actorGrid) {
            actors.addAll(actorRow)
        }
        return actors
    }
}
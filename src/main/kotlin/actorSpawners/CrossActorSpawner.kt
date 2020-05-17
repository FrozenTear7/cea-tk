package actorSpawners

import kotlinx.coroutines.channels.Channel
import messages.IMessage

class CrossActorSpawner(m: Int, n: Int, nIter: Int) : ActorSpawner(m, n, nIter) {
    override fun attachNeighbours() {
        for (i in 0 until m) {
            for (j in 0 until n) {
                val neighbours: MutableList<Channel<IMessage>> = ArrayList()

                if (i - 1 >= 0)
                    neighbours.add(actorGrid[i - 1][j].actorChannel)
                if (i + 1 <= m - 1)
                    neighbours.add(actorGrid[i + 1][j].actorChannel)
                if (j - 1 >= 0)
                    neighbours.add(actorGrid[i][j - 1].actorChannel)
                if (j + 1 <= n - 1)
                    neighbours.add(actorGrid[i][j + 1].actorChannel)

                actorGrid[i][j].setNeighbours(neighbours)
            }
        }
    }
}
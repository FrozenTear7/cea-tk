package actorSpawners

import kotlinx.coroutines.channels.Channel
import messages.IMessage

/*
    Little Square means 3x3 grid.
 */
class LittleSquareActorSpawner(m: Int, n: Int, nIter: Int) : ActorSpawner(m, n, nIter) {
    override fun attachNeighbours() {
        for (i in 0 until m) {
            for (j in 0 until n) {
                val neighbours: MutableList<Channel<IMessage>> = ArrayList()

                if (isInGrid(i - 1, j - 1))
                    neighbours.add(actorGrid[i - 1][j - 1].actorChannel)
                if (isInGrid(i, j - 1))
                    neighbours.add(actorGrid[i][j - 1].actorChannel)
                if (isInGrid(i + 1, j - 1))
                    neighbours.add(actorGrid[i + 1][j - 1].actorChannel)
                if (isInGrid(i - 1, j))
                    neighbours.add(actorGrid[i - 1][j].actorChannel)
                if (isInGrid(i + 1, j))
                    neighbours.add(actorGrid[i + 1][j].actorChannel)
                if (isInGrid(i - 1, j + 1))
                    neighbours.add(actorGrid[i - 1][j + 1].actorChannel)
                if (isInGrid(i, j + 1))
                    neighbours.add(actorGrid[i][j + 1].actorChannel)
                if (isInGrid(i + 1, j + 1))
                    neighbours.add(actorGrid[i + 1][j + 1].actorChannel)

                actorGrid[i][j].setNeighbours(neighbours)
            }
        }
    }

    private fun isInGrid(x: Int, y: Int): Boolean {
          return (x in 0 until m) && (y in 0 until n)
    }
}

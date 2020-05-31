package actorSpawners

import kotlinx.coroutines.channels.Channel
import messages.IMessage

/*
    Little Square means 3x3 grid.
 */
class LittleSquareActorSpawner(m: Int, n: Int, nIter: Int) : ActorSpawner(m, n, nIter) {
    override fun attachNeighbours() {
        for (x in 0 until m) {
            for (y in 0 until n) {
                val neighbours: MutableList<Channel<IMessage>> = ArrayList()

                for (i in -1..1) {
                    for (j in -1..1) {
                        if (i == 0 && j == 0) {
                            continue
                        }
                        if (isValidCoordinates(x + i, y + j)) {
                            neighbours.add(actorGrid[x + i][y + j].actorChannel)
                        }
                    }
                }

                actorGrid[x][y].setNeighbours(neighbours)
            }
        }
    }

    private fun isValidCoordinates(x: Int, y: Int): Boolean {
        return (x in 0 until m) && (y in 0 until n)
    }
}

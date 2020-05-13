package actorSpawners

import actors.Actor

class CrossActorSpawner(m: Int, n: Int) : ActorSpawner(m, n) {

    override fun attachNeighbours() {
        for (i in 0 until m) {
            for (j in 0 until n) {
                val neighbours: MutableList<Actor> = ArrayList()

                if (i - 1 >= 0)
                    neighbours.add(actorGrid[i - 1][j])
                if (i + 1 <= m - 1)
                    neighbours.add(actorGrid[i + 1][j])
                if (j - 1 >= 0)
                    neighbours.add(actorGrid[i][j - 1])
                if (j + 1 <= n - 1)
                    neighbours.add(actorGrid[i][j + 1])

                actorGrid[i][j].setNeighbours(neighbours)
            }
        }
    }
}
package genotypes

import utils.randrange
import kotlin.math.abs

// One of the benchmark functions used for testing: the Beale function.
// The task is to minimize this function for (x, y) in [-4.5, 4.5],
// but we allow other real inputs as their fitness is worse and should lead to the same extremum.

// The fitness formula is the negative Beale function, since we always maximize fitness (but want to find a minimum).

// Reproducing two genotypes yields their average +- small random value (mutation)

class GenotypeExample2(xInitial: Double = randrange(-4.5, 4.5), yInitial: Double = randrange(-4.5, 4.5)) : IGenotype {
    private val x = xInitial
    private val y = yInitial

    override fun fitness(): Double {
        return -(2.5 - x + x * y) * (2.5 - x + x * y) - (2.25 - x + x * y * y) * (2.25 - x + x * y * y) - (2.625 - x + x * y * y * y) * (2.625 - x + x * y * y * y)
    }

    override fun reproduce(other: IGenotype): IGenotype {
        other as GenotypeExample2

        val xNew = (x + other.x) / 2
        val xRandomOffset = (Math.random() - 0.5) * abs(x - other.x)
        val yNew = (y + other.y) / 2
        val yRandomOffset = (Math.random() - 0.5) * abs(x - other.x)

        return GenotypeExample2(xNew + xRandomOffset, yNew + yRandomOffset)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}
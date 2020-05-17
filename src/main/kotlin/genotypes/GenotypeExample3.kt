package genotypes

import utils.randrange
import kotlin.math.*

// One of the benchmark functions used for testing: the Ackley function.
// The task is to minimize this function for (x, y) in [-5, 5],
// but we allow other real inputs as their fitness should be worse and should lead to the same results.

// The fitness formula is the negative Ackley function, since we always maximize fitness (but want to find a minimum).

// Reproducing two genotypes yields their average +- small random value (mutation)

class GenotypeExample3(xInitial: Double = randrange(-5.0, 5.0), yInitial: Double = randrange(-5.0, 5.0)) : IGenotype {
    private val x = xInitial
    private val y = yInitial

    override fun fitness(): Double {
        return 20 * exp(-0.2 * sqrt(0.5 * (x * x + y * y))) + exp(0.5*(cos(2*PI*x) + cos(2*PI*y))) - exp(1.0) - 20
    }

    override fun reproduce(other: IGenotype): IGenotype {
        other as GenotypeExample3

        val xNew = (x + other.x) / 2
        val xRandomOffset = (Math.random() - 0.5) * abs(x - other.x) * 10
        val yNew = (y + other.y) / 2
        val yRandomOffset = (Math.random() - 0.5) * abs(x - other.x) * 10

        return GenotypeExample3(xNew + xRandomOffset, yNew + yRandomOffset)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}
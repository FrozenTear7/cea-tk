package genotypes

import kotlin.math.abs

// Simple optimisation problem: maximize the area of a rectangle with perimeter 2 and one side with length x.
// Negative x (and greater than 1, resulting in the other side having negative length) are not ruled out,
// but should yield negative area (fitness).

// Reproducing two genotypes yields their average +- small random value (mutation)

class GenotypeExample1(xInitial: Double = Math.random()) : IGenotype {
    private val x = xInitial

    override fun fitness() = x * (1 - x)
    override fun reproduce(other: IGenotype): IGenotype {
        other as GenotypeExample1

        val xNew = (x + other.x) / 2
        val diff = abs(x - other.x)
        val randomOffset = (Math.random() - 0.5) * diff

        return GenotypeExample1(xNew + randomOffset)
    }

    override fun toString(): String {
        return x.toString()
    }
}
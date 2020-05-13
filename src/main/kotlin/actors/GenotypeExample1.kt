package actors

// simple optimisation problem: maximize the area of a rectangle with perimeter 2 and one side with length x

// reproducing two genotypes yields their average +- small random value (mutation)

class GenotypeExample1(private val xInitial: Double = Math.random()) : IGenotype {
    private val x = xInitial

    override fun fitness() = x * (1 - x)
    override fun reproduce(other: IGenotype): GenotypeExample1 {
        val xNew = x + other.x // nie mogę założyć, że other ma property x bo nie jest typu GenotypeExample1
        return GenotypeExample1(xNew)
    }
}
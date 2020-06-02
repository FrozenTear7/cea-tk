package genotypeChoosers

import genotypes.IGenotype
import kotlin.random.Random

class GenotypeRandomChooser : IGenotypeChooser {
    override fun choose(genotypes: List<IGenotype>): Int {
        return Random.nextInt(0, genotypes.size)
    }
}
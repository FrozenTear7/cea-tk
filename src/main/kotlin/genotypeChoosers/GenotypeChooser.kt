package genotypeChoosers

import genotypes.IGenotype

interface GenotypeChooser {
    fun choose(genotypes: List<IGenotype>) : Int
}
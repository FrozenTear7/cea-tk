package genotypeChoosers

import genotypes.IGenotype

interface IGenotypeChooser {
    fun choose(genotypes: List<IGenotype>): Int
}
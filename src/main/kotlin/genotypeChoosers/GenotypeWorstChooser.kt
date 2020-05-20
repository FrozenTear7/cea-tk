package genotypeChoosers

import genotypes.IGenotype

class GenotypeWorstChooser : GenotypeChooser {

    override fun choose(genotypes: List<IGenotype>) : Int {
        var worstIndex = 0
        var worstFitness = Double.MAX_VALUE

        for (i in genotypes.indices) {
            if (genotypes[i].fitness() < worstFitness) {
                worstFitness = genotypes[i].fitness()
                worstIndex = i
            }
        }

        return worstIndex
    }
}
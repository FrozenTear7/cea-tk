package genotypeChoosers

import genotypes.IGenotype

class GenotypeBestChooser : GenotypeChooser {

    override fun choose(genotypes: List<IGenotype>) : Int {
        var bestIndex = -1
        var bestFitness = Double.MIN_VALUE

        for (i in genotypes.indices) {
            if (genotypes[i].fitness() > bestFitness) {
                bestFitness = genotypes[i].fitness()
                bestIndex = i
            }
        }

        return bestIndex
    }
}
package genotypeChoosers

import genotypes.IGenotype

class GenotypeBestChooser : GenotypeChooser {

    override fun choose(genotypes: List<IGenotype>) : Int {
        var bestIndex = 0
        var bestFitness = -Double.MAX_VALUE

        for (i in genotypes.indices) {
            if (genotypes[i].fitness() > bestFitness) {
                bestFitness = genotypes[i].fitness()
                bestIndex = i
            }
        }

        return bestIndex
    }
}
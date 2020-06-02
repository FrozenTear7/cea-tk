package genotypeChoosers

import genotypes.IGenotype

class GenotypeBestChooser : IGenotypeChooser {
    override fun choose(genotypes: List<IGenotype>): Int {
        var bestIndex = -1
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
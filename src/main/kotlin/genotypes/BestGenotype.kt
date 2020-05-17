package genotypes

import genotypes.IGenotype

class BestGenotype(genotype: IGenotype) {
    var timestamp: Long = System.currentTimeMillis()
    var genotype: IGenotype = genotype
        set(value) {
            if (value.fitness() > field.fitness()) {
                field = value
                timestamp = System.currentTimeMillis()
            }
        }
}
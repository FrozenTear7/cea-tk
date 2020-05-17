package genotypes

interface IGenotype{
    fun fitness() : Double
    fun reproduce(other : IGenotype) : IGenotype
}
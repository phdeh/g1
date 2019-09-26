import kotlin.math.*

class Specie {
    @FitnessFunction
    fun f() = sin(x) / x.pow(2)

    @Trait(from = 3.1, to = 20.0, resolution = 16)
    var x = Double.NaN
}

fun main() {
    val genetics = Genetics(
        initialPopulation = 20,
        waitingGenerations = 40,
        chanceOfMutation = 0.1,
        chanceOfCrossover = 0.1
    )
    // 7.59655
    println(Specie().solve(genetics, false).result.traitsToString())
}

fun <T> List<T>.generation(genetics: Genetics): List<T> {
    val new = this.reproduct()
    new.findPairs().forEach { it.first.crossover(it.second, genetics) }
    new.forEach { it.mutate(genetics) }
    return new
}

fun <T> T.solve(genetics: Genetics, silent: Boolean = true): Report<T> {
    var species = List(genetics.initialPopulation) { this.reproductSpecie() }
    var max = Double.MIN_VALUE
    var smax: T? = null
    var gen = 0
    var wait = genetics.waitingGenerations
    while (wait >= 0) {
        gen++
        if (!silent)
            println("Поколение ${gen}:")
        species = species.generation(genetics)
        if (!silent)
            println(species.map { it.traitsToString() })
        smax = species.maxBy { it.fitnessFunction() }
        val cmax = smax.fitnessFunction()
        if (cmax > max) {
            max = cmax
            wait = genetics.waitingGenerations
        } else {
            wait--
        }
    }
    return Report(smax, gen, genetics)
}

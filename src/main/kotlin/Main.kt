import kotlin.math.*

//class Specie {
//    @FitnessFunction
//    fun f() = sin(x) / x.pow(2)
//
//    @Trait(from = 3.1, to = 20.0, resolution = 16)
//    var x = Double.NaN
//}

class Specie {
    @FitnessFunction
    fun f() = -(0 until args.size).sumByDouble { (0..it).sumByDouble { args[it] }.pow(2) }

    @Trait(from = -65536.0, to = 65536.0, resolution = 32)
    var x = Double.NaN
    @Trait(from = -65536.0, to = 65536.0, resolution = 32)
    var y = Double.NaN
    @Trait(from = -65536.0, to = 65536.0, resolution = 32)
    var z = Double.NaN
    @Trait(from = -65536.0, to = 65536.0, resolution = 32)
    var w = Double.NaN
    @Trait(from = -65536.0, to = 65536.0, resolution = 32)
    var t = Double.NaN

    val args get() = listOf(x, y, z, w, t)
}

fun main() {
    val genetics = Genetics(
        initialPopulation = 20,
        waitingGenerations = 40,
        chanceOfMutation = 0.01,
        chanceOfCrossover = 0.01,
        crossoverMethod = CrossoverMethod.AVERAGE_CROSSOVER
    )
    // 7.59655
    println(Specie().solve(genetics, false).result.traitsToString())
}

fun <T> List<T>.generation(genetics: Genetics): List<T> {
    val new = this.reproduct()
    new.findPairs(2 / genetics.crossoverMethod.offspringNumber)
        .forEach { it.first.crossover(it.second, genetics) }
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

import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.pow

fun main() {
    val lock = Object()
    val table = List(9) { Array(CrossoverMethod.values().size) { "" } }
    GlobalScope.launch {
        val mt = mutableListOf<Deferred<*>>()
        for (population in (1 until 10)) {
            for (crossoverMethod in CrossoverMethod.values()) {
                mt.add(GlobalScope.async {
                    val genetics = Genetics(
                        initialPopulation = (population + 1).toDouble().pow(2).toInt(),
                        chanceOfCrossover = .5,
                        chanceOfMutation = .2,
                        waitingGenerations = 50,
                        crossoverMethod = crossoverMethod
                    )
                    val iter = 3
                    var sum = 0.0
                    var sum2 = 0.0
                    for (iteration in (1..iter)) {
                        sum += abs(Specie().solve(genetics).generations)
                        sum2 += abs(Specie().solve(genetics).result?.f() ?: 0.0)
                    }
                    table[population - 1][crossoverMethod.ordinal] = "${df.format(sum / iter)}, ${df.format(sum2 / iter)}"
                    println("Done $population, $crossoverMethod")
                })
            }
        }
        mt.awaitAll()
        synchronized(lock) {
            lock.notify()
        }
    }
    synchronized(lock) {
        lock.wait()
    }
    println("")
    CrossoverMethod.values().forEach {
        print(" & ${it.firstLetters}   ")
    }
    println("\\\\")
    println("\\midrule")
    table.forEachIndexed { i, it ->
        print("${(i + 2).toDouble().pow(2).toInt()}    ")
        it.forEach {
            print("& $it    ")
        }
        println("\\\\")
    }
}


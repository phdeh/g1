import kotlinx.coroutines.*
import kotlin.math.abs

fun main() {
    val lock = Object()
    val table = List(9) { Array(9) { "" } }
    GlobalScope.launch {
        val mt = mutableListOf<Deferred<*>>()
        for (population in (1 until 10)) {
            for (mutation in (1 until 10)) {
                mt.add(GlobalScope.async {
                    val genetics = Genetics(
                        initialPopulation = population * 5,
                        chanceOfCrossover = mutation * .1,
                        chanceOfMutation = mutation * .1
                    )
                    val iter = 50
                    var sum = 0.0
                    for (iteration in (1..iter))
                    //sum += Specie().solve(genetics).generations
                        sum += abs(Specie().solve(genetics).result?.f() ?: 0.0 - 7.59655)
                    table[population - 1][mutation - 1] = df.format(sum / iter)
                    println("Done $population, $mutation")
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
    table.forEach {
        it.forEach {
            print("& $it    ")
        }
        println("\\\\")
    }
}
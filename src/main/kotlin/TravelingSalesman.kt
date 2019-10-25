import java.lang.StringBuilder

class TravelingSalesman(private val graph: Graph) {
    var cities = uniques(graph.cities.size)

    val distance
        get() =
            cities.map { graph.cities[it] }.toCoilPairs()
                .sumByDouble { it.first distanceTo it.second }

    fun inherit(parent: TravelingSalesman) {
        cities.indices.forEach {
            cities[it] = parent.cities[it]
        }
    }

    fun copy(): TravelingSalesman {
        val ts = TravelingSalesman(graph)
        ts.inherit(this)
        return ts
    }

    fun mutate(prob: Int = 35) {
        do {
            val i = random.nextInt(cities.size)
            val j = anythingBut(i) { random.nextInt(cities.size) }
            val d = distance
            cities.switch(i, j)
            if (distance > d && random.nextInt(prob) != 0) {
                cities.switch(i, j)
                continue
            }
        } while (false)
    }

    fun crossover(other: TravelingSalesman) {
        val i = random.nextInt(cities.size)
        val a = cities[i]
        val j = other.cities.indexOf(a)
        cities[i] = other.cities[i]
        other.cities[i] = a
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(distance)
        sb.append(": ")
        cities.forEach {
            sb.append("$it.")
        }
        return sb.toString()
    }
}

fun main() {
    val graph = Graph("att48.tsp")
    val population = 250
    var generation = (1..population).map { TravelingSalesman(graph) }
    var minimum = Double.MAX_VALUE
    var minimumGen = 0
    var highMutation = 100

    var currGen = 0
    while (currGen - minimumGen <= 500 ) {
        if (highMutation > 0)
            highMutation--
        currGen++
        val currMin = generation.minBy { it.distance }?.distance ?: Double.MAX_VALUE
        println("Gen $currGen: $currMin")
        generation.minBy { it.distance }!!.cities.map { Dot(graph.cities[it].x, graph.cities[it].y) }
            .plotTo("output/${String.format("%05d", currGen)}.png")
        if (minimum > currMin) {
            minimum = currMin
            minimumGen = currGen
        }
        val nextGen = generation.sortedBy { it.distance }.reproductWithCoin().map { it.copy() }
        (nextGen.size - 5 until nextGen.size).forEach {
            nextGen[it].cities = uniques(graph.cities.size)
        }
        (0..10).forEach {
            nextGen.forEach { it.mutate() }
        }
        if (currGen - minimumGen <= 50 && highMutation == 0) {
            (0..50).forEach {
                nextGen.forEach { it.mutate(1) }
            }
            highMutation = 100
        }
        generation = nextGen
        generation.forEach {
            println("   $it")
        }
    }

}

fun <T> List<T>.reproductWithCoin(): List<T> {
    val ml = mutableListOf<T>()
    val it = iterator()
    var c = it.next()
    while (ml.size < size) {
        ml += c
        if (random.nextInt(5) == 0 && it.hasNext())
            c = it.next()
    }
    return ml
}
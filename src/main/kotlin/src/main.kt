package src

import kotlin.math.pow

val depth = 5
val newbies = 100
val mutation = 0.02
val reproduce = 20
val allVariables = 8

fun main() {
    val func = {args: DoubleArray -> (args.indices).sumByDouble { (0..it).sumByDouble { i -> args[i] }.pow(2) }}
    //val func = {args: DoubleArray -> (args).sumByDouble { it.pow(2) }}
    //val func = {args: DoubleArray -> (args).sumByDouble { it }}

    val win = Window()
    var generation = List(500) { Operator(depth, 0) }
    var totalMax = Operator(depth, 0)
    var totalMaxValue = Double.MIN_VALUE
    val list = deviationFrom(-5.536..65.536, allVariables, 2000) { func(it) }
//val list = deviationFrom(-5.12..5.12, allVariables, 2000) { func(it) }
    for (i in 0 until 1000) {
        val sorted = generation.map {
            it to it.deviationFrom(list, allVariables) { args ->
                func(args)
            }
        }.sortedBy { it.second }.map { it.first }
        val max = sorted.first()
        win.formula = max.toString()
            val dev = max.deviationFrom(list, allVariables) { args ->
            func(args)
        }
        win.progress(dev)
        println(dev)
        if (totalMaxValue < dev) {
            totalMax = max
            totalMaxValue = dev
        }

        val newGeneration = mutableListOf<Operator>()
        newGeneration.add(max.clone())
        newGeneration.add(totalMax.clone())
        for (j in 1 until generation.size - newbies - 1) {
            var k = 0
            var max = max
            while (random.nextInt(reproduce) != 0 && k < j)
                k++
            max = sorted[k]
            val cur = max.clone()
            if (cur is Operator) {
                cur.mutate()
            }
            newGeneration.add(cur)
        }
        for (j in 0 until newbies) {
            newGeneration.add(Operator(depth, 0))
        }
//        newGeneration.forEach {
//            println(it)
//        }
        generation = newGeneration
    }
}
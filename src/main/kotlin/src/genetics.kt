package src

import sync
import kotlin.math.pow
import kotlin.math.sqrt

fun deviationFrom(
    doubleRange: ClosedFloatingPointRange<Double>,
    args: Int = 8,
    iterations: Int = 200,
    func: (DoubleArray) -> Double
): List<DoubleArray> {
    val ml = mutableListOf<DoubleArray>()
    for (i in 0 until iterations) {
        ml += DoubleArray(args) {
            random.nextDouble() *
                    (doubleRange.endInclusive - doubleRange.start) +
                    doubleRange.start
        }
    }
    return ml
}

fun Operator.deviationFrom(
    list: List<DoubleArray>,
    args: Int = 8,
    iterations: Int = 200,
    threads: Int = 16,
    func: (DoubleArray) -> Double
): Double {
    val sds = DoubleArray(threads)
    var sd = 0.0
    for (i in 0 until iterations) {
        val da = list[i]
        val gen = this.calculate(da)
        val org = func(da)
        sd += (gen - org).pow(2)
    }
    val result = sqrt(sd / iterations)
    return if (result.isNaN()) Double.MAX_VALUE else result
}

fun Operator.deviationFrom(
    doubleRange: ClosedFloatingPointRange<Double>,
    args: Int = 8,
    iterations: Int = 200,
    threads: Int = 16,
    func: (DoubleArray) -> Double
): Double {
    val sds = DoubleArray(threads)
    val da = DoubleArray(args)
    var sd = 0.0
    for (i in 0 until iterations) {
        for (j in da.indices)
            da[j] = random.nextDouble() *
                    (doubleRange.endInclusive - doubleRange.start) +
                    doubleRange.start
        val gen = this.calculate(da)
        val org = func(da)
        sd += (gen - org).pow(2)
        if (sd.isNaN())
            return Double.MAX_VALUE
    }
    val result = sqrt(sd / iterations)
    return if (result.isNaN()) Double.MAX_VALUE else result
}
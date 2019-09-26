import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

const val DEFAULT_RESOLUTION = 16
val df = DecimalFormat("#.###")

@Target(AnnotationTarget.FIELD)
annotation class Trait(
    val from: Double,
    val to: Double,
    val resolution: Int = DEFAULT_RESOLUTION
)

@Target(AnnotationTarget.FUNCTION)
annotation class FitnessFunction

fun Double.toBitSet(
    range: ClosedFloatingPointRange<Double>,
    resolution: Int = DEFAULT_RESOLUTION
): BitSet {
    val from = range.start
    val to = range.endInclusive
    val times = BigDecimal(2).pow(resolution)
    val numerator = BigDecimal(this).multiply(times).add(BigDecimal(from).multiply(times))
    val denominator = BigDecimal(to).subtract(BigDecimal(from))
    val bi = numerator.divide(denominator, 20, RoundingMode.HALF_UP).toBigInteger()
    val bs = BitSet()
    for (i in 0 until resolution)
        bs[i] = bi.shiftRight(i).and(BigInteger.ONE).toByte() == 1.toByte()
    return bs
}

fun BitSet.toDouble(
    range: ClosedFloatingPointRange<Double>,
    resolution: Int = DEFAULT_RESOLUTION
): Double {
    val from = range.start
    val to = range.endInclusive
    var bi = BigInteger.ZERO
    for (i in 0 until resolution)
        if (this[i])
            bi = bi or (BigInteger.ONE shl i)
    val times = BigDecimal(2).pow(resolution)
    val numerator = bi.toBigDecimal().divide(times, 20, RoundingMode.HALF_UP)
        .multiply(BigDecimal(to).subtract(BigDecimal(from)))
    return numerator.subtract(BigDecimal(from)).toDouble()
}
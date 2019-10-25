import java.awt.geom.Point2D
import java.lang.UnsupportedOperationException

typealias Dot = Point2D.Double

fun min(dots: List<Dot>, value: (Dot) -> Double): Double {
    return value(dots.minBy { value(it) } ?: throw UnsupportedOperationException())
}

fun max(dots: List<Dot>, value: (Dot) -> Double): Double {
    return value(dots.maxBy { value(it) } ?: throw UnsupportedOperationException())
}

fun List<Dot>.to2Pi(): List<Dot> {
    val min = min(this) { it.x }
    val max = max(this) { it.x }
    return map { Dot((it.x - min) / (max - min) * Math.PI * 2 - Math.PI, it.y) }
}
import java.io.File
import java.util.*
import kotlin.math.hypot

class Graph(path: String) {
    inner class City(val x: Double, val y: Double) {
        infix fun distanceTo(other: City) = hypot(x - other.x, y - other.y)
    }

    val cities: List<City>

    init {
        val c = mutableListOf<City>()
        val sc = Scanner(File(path))
        while (sc.hasNextLine()) {
            val s = sc.nextLine().split(Regex("\\s+"))
            c += City(s[1].toDouble(), s[2].toDouble())
        }
        cities = c
    }
}
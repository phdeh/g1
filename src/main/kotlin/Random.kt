import java.util.*

val random = Random()

fun Random.nextInt(bound: Int, andAlso: (Int) -> Boolean): Int {
    var i: Int
    do {
        i = this.nextInt(bound)
    } while (!andAlso(i))
    return i
}
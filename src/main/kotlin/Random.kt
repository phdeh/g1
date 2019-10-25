import java.lang.UnsupportedOperationException
import java.util.*

val random = Random()

fun Random.nextInt(bound: Int, andAlso: (Int) -> Boolean): Int {
    var i: Int
    do {
        i = this.nextInt(bound)
    } while (!andAlso(i))
    return i
}

fun uniques(length: Int): MutableList<Int> {
    val ml = mutableListOf<Int>()
    val list = (0 until length).toMutableList()
    for (i in 0 until length) {
        val j = random.nextInt(list.size)
        ml += list[j]
        list.removeAt(j)
    }
    return ml
}

fun <T> List<T>.toPairs(): List<Pair<T, T>> {
    var prev: T? = null
    val ml = mutableListOf<Pair<T, T>>()
    for (i in this) {
        if (prev != null)
            ml += Pair(prev, i)
        prev = i
    }
    return ml
}

fun <T> List<T>.toCoilPairs(): List<Pair<T, T>> {
    var prev: T? = this.last()
    val ml = mutableListOf<Pair<T, T>>()
    for (i in this) {
        if (prev != null)
            ml += Pair(prev, i)
        prev = i
    }
    return ml
}

fun <T> anythingBut(vararg t: T, action: () -> T): T {
    val timeout = 10000
    for (i in 0 until timeout) {
        val i = action()
        for (j in t)
            if (i == t)
                continue
        return i
    }
    throw RuntimeException()
}

fun<T> MutableList<T>.switch(i: Int, j: Int): MutableList<T> {
    val a = this[i]
    this[i] = this[j]
    this[j] = a
    return this
}
package src

import java.util.*

val random = Random()

fun<T> Array<T>.randomMathing(matcher: (T) -> Boolean): T {
    for (i in 0..1000) {
        val t = this.random()
        if (matcher(t))
            return t
    }
    return this.random()
}
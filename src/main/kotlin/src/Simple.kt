
import kotlinx.coroutines.*

fun main() {
    println("Start")
    sync {
        (0..10).map {
            async { println(it) }
        }.awaitAll()
    }
    println("Stop")
}

inline fun sync(crossinline async: suspend CoroutineScope.() -> Unit) {
    val mutex = Object()
    GlobalScope.launch {
        this.async()
        synchronized(mutex) { mutex.notify() }
    }
    synchronized(mutex) {
        mutex.wait()
    }
}


data class Report<T>(
    val result: T?,
    val generations: Int,
    val genetics: Genetics
)

data class Genetics(
    val initialPopulation: Int = 10,
    val waitingGenerations: Int = 5,
    val chanceOfMutation: Double = 1.0,
    val chanceOfCrossover: Double = 1.0,
    val crossoverMethod: CrossoverMethod = CrossoverMethod.ONE_POINT_CROSSOVER
)

fun <T> List<T>.reproduct(): List<T> {
    val ml = mutableListOf<T>()
    val min = this.minBy { it.fitnessFunction() }.fitnessFunction()
    val sum = this.sumByDouble { it.fitnessFunction() - min }
    this.forEach {
        var rnd = random.nextDouble() * sum
        var lsum = 0.0
        this.forEach {
            lsum += it.fitnessFunction() - min
            if (rnd <= lsum) {
                ml.add(it.reproductSpecie())
                rnd = Double.MAX_VALUE
            }
        }
    }
    return ml.toList()
}

fun <T> T.reproductSpecie(): T {
    val new = (this as Any)::class.java.constructors.first().newInstance()
    new::class.java.declaredFields.forEach {
        it.isAccessible = true
        val cur = it.getDouble(this)
        val ann = it.getAnnotation(Trait::class.java)
        if (ann != null && cur.isNaN())
            it.setDouble(new, random.nextDouble() * (ann.to - ann.from) + ann.from)
        else
            it.setDouble(new, cur)
    }
    @Suppress("UNCHECKED_CAST")
    return new as T
}

fun <T> T.mutate(genetics: Genetics): T {
    val new = this as Any
    new::class.java.declaredFields.forEach {
        it.isAccessible = true
        val cur = it.getDouble(this)
        val ann = it.getAnnotation(Trait::class.java)
        if (ann != null && random.nextDouble() < genetics.chanceOfMutation) {
//            do {
//                val i = random.nextInt(ann.resolution)
//                val b = cur.toBitSet(ann.from..ann.to, ann.resolution)
//                b[i] = !b[i]
//                if (b.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to)
//                    continue
//                it.setDouble(new, b.toDouble(ann.from..ann.to, ann.resolution))
//            } while (false)
            do {
                val r = (random.nextDouble() * (ann.to - ann.from) + ann.from) / 10.0
                if (cur + r !in ann.from..ann.to)
                    continue
                it.setDouble(new, cur + r)
            } while (false)
        }
    }
    @Suppress("UNCHECKED_CAST")
    return new as T
}

fun <T> T.crossover(other: T, genetics: Genetics) {
    val new = this as Any
    new::class.java.declaredFields.forEach {
        it.isAccessible = true
        val ann = it.getAnnotation(Trait::class.java)
        if (ann != null && random.nextDouble() < genetics.chanceOfCrossover) {
            val cur1 = it.getDouble(this)
            val cur2 = it.getDouble(other)
            val result = genetics.crossoverMethod.action(
                cur1, cur2, ann
            )
            if (genetics.crossoverMethod.offspringNumber >= 1)
                it.setDouble(this, result.first)
            if (genetics.crossoverMethod.offspringNumber >= 2)
                it.setDouble(other, result.second)
        }
    }
}


fun <T> T.traitsToString(): String {
    val str = mutableListOf<String>()
    (this as Any)::class.java.declaredMethods.forEach {
        it.isAccessible = true
        val ann = it.getAnnotation(FitnessFunction::class.java)
        if (ann != null) {
            val str2 = mutableListOf<String>()
            (this as Any)::class.java.declaredFields.forEach {
                it.isAccessible = true
                val ann = it.getAnnotation(Trait::class.java)
                if (ann != null) {
                    str2.add(it.name)
                }
            }
            str.add("f(${str2.joinToString(", ")})=${df.format(it.invoke(this))}")
        }
    }
    (this as Any)::class.java.declaredFields.forEach {
        it.isAccessible = true
        val cur = it.getDouble(this)
        val ann = it.getAnnotation(Trait::class.java)
        if (ann != null) {
            str.add("${it.name}=${df.format(it.getDouble(this))}")
        }
    }
    return "${(this as Any)::class.java.simpleName}{${str.joinToString(", ")}}"
}

fun <T> T.fitnessFunction(): Double {
    val str = mutableListOf<String>()
    (this as Any)::class.java.declaredMethods.forEach {
        it.isAccessible = true
        val ann = it.getAnnotation(FitnessFunction::class.java)
        if (ann != null) {
            return it.invoke(this) as Double
        }
    }
    return Double.NaN
}

operator fun <T, P> Collection<T>.times(other: Collection<P>): List<Pair<T, P>> {
    val list = mutableListOf<Pair<T, P>>()
    this.forEach { i ->
        other.forEach { j ->
            list.add(Pair(i, j))
        }
    }
    return list
}

fun <T> List<T>.findPairs(): List<Pair<T, T>> {
    val curr = this.toMutableList()
    val list = mutableListOf<Pair<T, T>>()
    while (curr.size >= 2) {
        val i = 0
        val j = random.nextInt(curr.size) { it != i }
        list.add(Pair(curr[i], curr[j]))
        curr.removeAt(j)
        curr.removeAt(i)
    }
    return list
}

fun <T> List<T>.findPairs(times: Int): List<Pair<T, T>> {
    val curr = mutableListOf<Pair<T, T>>()
    for (i in 1..times)
        curr.addAll(this.findPairs())
    return curr
}
enum class CrossoverMethod(
    val offspringNumber: Int = 1,
    val action: (Double, Double, Trait) -> Pair<Double, Double> = { i, j, _ -> Pair(i, j) }
) {
    SINGLE_CROSSOVER(2, { cur1, cur2, ann ->
        var res1 = 0.0
        var res2 = 0.0
        do {
            val i = random.nextInt(ann.resolution)
            val b1 = cur1.toBitSet(ann.from..ann.to, ann.resolution)
            val b2 = cur2.toBitSet(ann.from..ann.to, ann.resolution)
            val b1i = b1[i]
            b1[i] = b2[i]
            b2[i] = b1i
            if (b1.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to ||
                b2.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to
            )
                continue
            res1 = b1.toDouble(ann.from..ann.to, ann.resolution)
            res2 = b2.toDouble(ann.from..ann.to, ann.resolution)
        } while (false)
        Pair(res1, res2)
    }),

    ONE_POINT_CROSSOVER(2, { cur1, cur2, ann ->
        var res1 = 0.0
        var res2 = 0.0
        do {
            val i = random.nextInt(ann.resolution)
            val b1 = cur1.toBitSet(ann.from..ann.to, ann.resolution)
            val b2 = cur2.toBitSet(ann.from..ann.to, ann.resolution)
            for (j in i until ann.resolution) {
                val b1j = b1[j]
                b1[j] = b2[j]
                b2[j] = b1j
            }
            if (b1.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to ||
                b2.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to
            )
                continue
            res1 = b1.toDouble(ann.from..ann.to, ann.resolution)
            res2 = b2.toDouble(ann.from..ann.to, ann.resolution)
        } while (false)
        Pair(res1, res2)
    }),

    N_POINT_CROSSOVER(2, { cur1, cur2, ann ->
        var res1 = 0.0
        var res2 = 0.0
        do {
            val b1 = cur1.toBitSet(ann.from..ann.to, ann.resolution)
            val b2 = cur2.toBitSet(ann.from..ann.to, ann.resolution)
            for (j in 0 until ann.resolution) {
                if (random.nextBoolean())
                    continue
                val b1j = b1[j]
                b1[j] = b2[j]
                b2[j] = b1j
            }
            if (b1.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to ||
                b2.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to
            )
                continue
            res1 = b1.toDouble(ann.from..ann.to, ann.resolution)
            res2 = b2.toDouble(ann.from..ann.to, ann.resolution)
        } while (false)
        Pair(res1, res2)
    }),

    DISCRETE_CROSSOVER(1, { cur1, cur2, ann ->
        var res1 = 0.0
        do {
            val b1 = cur1.toBitSet(ann.from..ann.to, ann.resolution)
            val b2 = cur2.toBitSet(ann.from..ann.to, ann.resolution)
            for (i in 0 until ann.resolution)
                b1[i] = b1[i] or b2[i]
            if (b1.toDouble(ann.from..ann.to, ann.resolution) !in ann.from..ann.to)
                continue
            res1 = b1.toDouble(ann.from..ann.to, ann.resolution)
        } while (false)
        Pair(res1, Double.NaN)
    }),

    AVERAGE_CROSSOVER(1, { cur1, cur2, ann ->
        Pair((cur1 + cur2) / 2.0, Double.NaN)
    }),

    ARITHMETICAL_CROSSOVER(2, { cur1, cur2, ann ->
        val w = random.nextDouble()
        val v = 1 - w
        Pair(cur1 * w + cur2 * v, cur1 * v + cur2 * w)
    }),

    ;

    val firstLetters = let {
        val sb = StringBuilder()
        var f = true
        name.forEach {
            if (it.isLetter()) {
                if (f)
                    sb.append(it)
                f = false
            } else
                f = true
        }
        sb.toString()
    }
}
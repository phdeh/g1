package src

import kotlin.math.*
import java.text.DecimalFormat


class OperatorContainer {
    private val level: Int

    var operator: Operand

    constructor(level: Int = 5) {
        this.level = level
        operator = randomize()
    }

    private constructor(other: OperatorContainer) {
        this.level = other.level
        this.operator = other.operator.clone()
    }

    fun randomize(): Operand {
        val operator = if (level <= 0)
            Variable(random.nextInt(allVariables * 2) - allVariables)
        else
            Operator(level - 1, abs(random.nextInt()))
        this.operator = operator
        return operator
    }

    fun mutate(probability: Double = 0.001) {
        operator.mutate()
    }

    fun clone() = OperatorContainer(this)

    override fun toString() = operator.toString()
}


interface Operand {
    val operators: List<OperatorContainer>
    fun calculate(variables: DoubleArray): Double
    fun clone(): Operand
    fun mutate(probability: Double = mutation)
}

class Variable(
    private var index: Int,
    private var value: Double = random.nextDouble() * 10 - 5
) : Operand {
    override val operators: List<OperatorContainer>
        get() = listOf()

    override fun calculate(variables: DoubleArray): Double {
        return if (index <= 0)
            value
        else
            variables[(index - 1) % variables.size]
    }

    companion object {
        val df2 = DecimalFormat("0.00")
    }

    override fun toString(): String {
        return if (index <= 0)
            df2.format(value).replace(".", "{,}")
        else
            "{x_{${index}}}"
    }

    override fun clone(): Operand {
        return Variable(index)
    }

    override fun mutate(probability: Double) {
        if (random.nextDouble() <= probability) {
            index = random.nextInt(allVariables * 2) - allVariables + 1
        }
        if (random.nextDouble() <= probability) {
            value += random.nextDouble() * 10 - 5
        }
    }
}

class Operator : Operand {
    enum class Operation(
        val args: Int,
        val action: (Double, Double) -> Double,
        val stringifier: (List<OperatorContainer>) -> String
    ) {
        NONE(1, { a, _ -> a }, { "${it.first()}" }),
        NONE_2(2, { a, _ -> a }, { "${it.first()}" }),
        NONE_3(2, { _, b -> b }, { "${it.last()}" }),
        ADD(2, { a, b -> a + b }, { "{\\left(${it.first()} + ${it.last()}\\right)}" }),
        SUBTRACT(2, { a, b -> a - b }, { "{\\left(${it.first()} - ${it.last()}\\right)}" }),
        MULTIPLY(2, { a, b -> a * b }, { "{\\left(${it.first()} \\cdot ${it.last()}\\right)}" }),
        DIVIDE(2, { a, b -> a / b }, { "{\\left(\\cfrac{${it.first()}}{${it.last()}}\\right)}" }),
        POWER(2, { a, b -> a.pow(b) }, { "{${it.first()}^{${it.last()}}}" }),
        ABSOLUTE(1, { a, _ -> abs(a) }, { "{\\left|${it.first()}\\right|}" }),
        SINE(1, { a, _ -> sin(a) }, { "{\\sin\\left(${it.first()}\\right)}" }),
        COSINE(1, { a, _ -> cos(a) }, { "{\\cos\\left(${it.first()}\\right)}" }),
        EXPONENT(1, { a, _ -> exp(a) }, { "{e^{${it.first()}}}" })
    }

    val level: Int
    var operation: Operation
    private val _operators: List<OperatorContainer>

    constructor(
        level: Int,
        index: Int,
        operation: Operation = Operation.values()[index % Operation.values().size]
    ) {
        this.level = level
        this.operation = operation
        _operators = List(operation.args) { OperatorContainer(level) }
    }

    private constructor(
        other: Operator
    ) {
        this.level = other.level
        this.operation = other.operation
        _operators = List(operation.args) { other._operators[it].clone() }
    }

    override val operators: List<OperatorContainer>
        get() = _operators

    override fun calculate(variables: DoubleArray): Double {
        return operation.action(
            _operators[0].operator.calculate(variables),
            if (_operators.size > 1) _operators[1].operator.calculate(variables) else Double.NaN
        )
    }

    override fun mutate(probability: Double) {
        for (i in operators.indices)
            if (random.nextDouble() <= probability) {
                operators[i].randomize()
            } else if (random.nextDouble() <= probability) {
                operation = Operation.values().randomMathing { it.args == operation.args }
            } else {
                operators[i].mutate(probability)
            }
    }

    override fun clone() = Operator(this)

    override fun toString(): String = operation.stringifier(operators)
}
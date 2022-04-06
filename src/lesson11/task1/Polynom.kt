@file:Suppress("UNUSED_PARAMETER")


import java.lang.ArithmeticException
import kotlin.math.pow

/**
 * Класс "полином с вещественными коэффициентами".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса -- полином от одной переменной (x) вида 7x^4+3x^3-6x^2+x-8.
 * Количество слагаемых неограничено.
 *
 * Полиномы можно складывать -- (x^2+3x+2) + (x^3-2x^2-x+4) = x^3-x^2+2x+6,
 * вычитать -- (x^3-2x^2-x+4) - (x^2+3x+2) = x^3-3x^2-4x+2,
 * умножать -- (x^2+3x+2) * (x^3-2x^2-x+4) = x^5+x^4-5x^3-3x^2+10x+8,
 * делить с остатком -- (x^3-2x^2-x+4) / (x^2+3x+2) = x-5, остаток 12x+16
 * вычислять значение при заданном x: при x=5 (x^2+3x+2) = 42.
 *
 * В конструктор полинома передаются его коэффициенты, начиная со старшего.
 * Нули в середине и в конце пропускаться не должны, например: x^3+2x+1 --> Polynom(1.0, 2.0, 0.0, 1.0)
 * Старшие коэффициенты, равные нулю, игнорировать, например Polynom(0.0, 0.0, 5.0, 3.0) соответствует 5x+3
 */
class Polynom(vararg coeffs: Double) {

    val coeffList = mutableListOf<Double>()


    init {
        for (i in coeffs.dropWhile { it == 0.0 }.reversed()) coeffList += i
    }


    /**
     * Геттер: вернуть значение коэффициента при x^i
     */
    fun coeff(i: Int): Double = if (i < coeffList.size) coeffList[i]
    else 0.0


    /**
     * Расчёт значения при заданном x
     */
    fun getValue(x: Double): Double = coeffList.foldIndexed(0.0) { index, sum, coeff ->
        sum + coeff * x.pow(index)
    }


    /**
     * Степень (максимальная степень x при ненулевом слагаемом, например 2 для x^2+x+1).
     *
     * Степень полинома с нулевыми коэффициентами считать равной 0.
     * Слагаемые с нулевыми коэффициентами игнорировать, т.е.
     * степень 0x^2+0x+2 также равна 0.
     */
    fun degree(): Int = maxOf(0, coeffList.lastIndex)

    /**
     * Сложение
     */
    operator fun plus(other: Polynom): Polynom {
        val resultCoeffs = mutableListOf<Double>()
        for (i in 0..maxOf(this.degree(), other.degree())) {
            resultCoeffs.add(this.coeff(i) + other.coeff(i))
        }
        return Polynom(*resultCoeffs.toDoubleArray().reversedArray())
    }

    /**
     * Смена знака (при всех слагаемых)
     */
    operator fun unaryMinus(): Polynom = Polynom(*coeffList.map { -it }.toDoubleArray().reversedArray())

    /**
     * Вычитание
     */
    operator fun minus(other: Polynom): Polynom = this + (-other)

    /**
     * Умножение
     */
    operator fun times(other: Polynom): Polynom {
        val resultCoeffs = MutableList(this.degree() + other.degree() + 1) { 0.0 }
        this.coeffList.forEachIndexed { i, thisCoeff ->
            other.coeffList.forEachIndexed { j, otherCoeff ->
                resultCoeffs[i + j] += thisCoeff * otherCoeff
            }
        }
        return Polynom(*resultCoeffs.toDoubleArray().reversedArray())
    }


    private fun divide(other: Polynom): Pair<Polynom, Polynom> {
        if (other.degree() == 0) return divide(other.coeff(0))
        var quotinent = Polynom()
        var remainder = Polynom(*coeffList.reversed().toDoubleArray())
        while (remainder.degree() >= other.degree()) {
            val multiplierDegree = remainder.degree() - other.degree()
            val multiplierCoeff = remainder.coeff(remainder.degree()) / other.coeff(other.degree())
            val multiplier = Polynom(*DoubleArray(multiplierDegree + 1) { i ->
                if (i == multiplierDegree) multiplierCoeff
                else 0.0
            }.reversedArray())
            quotinent += multiplier
            remainder -= multiplier * other
        }
        return quotinent to remainder
    }

    private fun divide(n: Double): Pair<Polynom, Polynom> {
        if (n == 0.0) throw ArithmeticException("Cannot divide by zero")
        return Polynom(
            *coeffList.map { it / n }.reversed().toDoubleArray()
        ) to Polynom()
    }

    /**
     * Деление
     *
     * Про операции деления и взятия остатка см. статью Википедии
     * "Деление многочленов столбиком". Основные свойства:
     *
     * Если A / B = C и A % B = D, то A = B * C + D и степень D меньше степени B
     */
    operator fun div(other: Polynom): Polynom = divide(other).first

    /**
     * Взятие остатка
     */
    operator fun rem(other: Polynom): Polynom = divide(other).second


    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Polynom) return false
        return coeffList == other.coeffList
    }


    /**
     * Получение хеш-кода
     */
    override fun hashCode() = coeffList.hashCode()
}

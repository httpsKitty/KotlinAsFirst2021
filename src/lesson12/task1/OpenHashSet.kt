@file:Suppress("UNUSED_PARAMETER")

package lesson12.task1

/**
 * Класс "хеш-таблица с открытой адресацией"
 *
 * Общая сложность задания -- сложная, общая ценность в баллах -- 20.
 * Объект класса хранит данные типа T в виде хеш-таблицы.
 * Хеш-таблица не может содержать равные по equals элементы.
 * Подробности по организации см. статью википедии "Хеш-таблица", раздел "Открытая адресация".
 * Методы: добавление элемента, проверка вхождения элемента, сравнение двух таблиц на равенство.
 * В этом задании не разрешается использовать библиотечные классы HashSet, HashMap и им подобные,
 * а также любые функции, создающие множества (mutableSetOf и пр.).
 *
 * В конструктор хеш-таблицы передаётся её вместимость (максимальное количество элементов)
 */
class OpenHashSet<T>(val capacity: Int) {

    /**
     * Массив для хранения элементов хеш-таблицы
     */
    internal val elements = Array<Any?>(capacity) { null }

    /**
     * Число элементов в хеш-таблице
     */
    private var sizeImpl = 0
    val size: Int
        get() = sizeImpl

    /**
     * Признак пустоты
     */
    fun isEmpty(): Boolean = size == 0

    /**
     * Добавление элемента.
     * Вернуть true, если элемент был успешно добавлен,
     * или false, если такой элемент уже был в таблице, или превышена вместимость таблицы.
     */
    fun add(element: T): Boolean {
        if (size == capacity) return false
        var index = element.hashCode() % capacity
        if (elements[index] == element) return false
        while (elements[index] != null) index = (index + 1) % capacity
        elements[index] = element
        sizeImpl++
        return true
    }

    /**
     * Проверка, входит ли заданный элемент в хеш-таблицу
     */
    operator fun contains(element: T) = containsObject(element)

    private fun containsObject(any: Any?): Boolean {
        val startIndex = any.hashCode() % capacity
        if (elements[startIndex] == any) return true
        var index = (startIndex + 1) % capacity
        while (any != elements[index] && index != startIndex) index = (index + 1) % capacity
        return index != startIndex
    }

    /**
     * Таблицы равны, если в них одинаковое количество элементов,
     * и любой элемент из второй таблицы входит также и в первую
     */

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpenHashSet<*>

        if (size != other.size) return false

        for (otherElement in other.elements) {
            if (otherElement != null && !this.containsObject(otherElement)) return false
        }

        return true
    }

    override fun hashCode(): Int = elements.sumOf { it?.hashCode() ?: 0}
}
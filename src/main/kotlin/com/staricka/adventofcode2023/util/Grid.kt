package com.staricka.adventofcode2023.util

import java.util.function.Function
import kotlin.math.max
import kotlin.math.min

/**
 * Utility class that represents a 2D grid of values
 */
open class Grid<T>(private val cells: HashMap<Int, HashMap<Int, T?>> = HashMap()) {
    fun clone(): Grid<T> = Grid(
        cells.entries.associateTo(HashMap()) { (k,v) -> k to v.entries.associateTo(HashMap()) { (k1, v1) -> k1 to v1 } }
    )

    private var minXInner: Int? = null
    private var maxXInner: Int? = null
    private var minYInner: Int? = null
    private var maxYInner: Int? = null

    val minX get() = minXInner!!
    val maxX get() = maxXInner!!
    val minY get() = minYInner!!
    val maxY get() = maxYInner!!

    operator fun get(x: Int, y: Int): T? = cells[x]?.get(y)
    fun get(p: Pair<Int, Int>): T? = this[p.first, p.second]
    operator fun set(x: Int, y: Int, value: T?) {
        cells.computeIfAbsent(x){HashMap()}[y] = value
        minXInner = min(minXInner ?: Int.MAX_VALUE, x)
        maxXInner = max(maxXInner ?: Int.MIN_VALUE, x)
        minYInner = min(minYInner ?: Int.MAX_VALUE, y)
        maxYInner = max(maxYInner ?: Int.MIN_VALUE, y)
    }
    fun remove(x: Int, y: Int) {
        cells[x]?.remove(y)
        if (cells[x]?.isEmpty() == true) {
            cells.remove(x)
        }

        // recompute min/max
        minXInner = cells.keys.minOrNull()
        maxXInner = cells.keys.maxOrNull()
        minYInner = cells.values.flatMap { it.keys }.minOrNull()
        maxYInner = cells.values.flatMap { it.keys }.maxOrNull()
    }

    fun cells(): List<Triple<Int, Int, T>> {
        return cells.entries.flatMap {
            (x, e) -> e.entries.filter { (_, v) -> v != null }
                .map { (y, v) -> Triple(x,y,v!!) }
        }
    }

    fun row(x: Int): List<Triple<Int, Int, T>> = cells[x]?.filter { (_,v) -> v != null }?.map { (y, v) -> Triple(x,y,v!!) } ?: emptyList()
    fun col(y: Int): List<Triple<Int, Int, T>> = cells.flatMap { (x, e) ->
        e.filter { (yc, v) -> y == yc && v != null }.map { (_, v) -> Triple(x,y,v!!) }
    }

    fun left(x: Int, y: Int) = Triple(x, y-1, this[x, y-1])
    fun right(x: Int, y: Int) = Triple(x, y+1, this[x, y+1])
    fun up(x: Int, y: Int) = Triple(x-1, y, this[x-1, y])
    fun down(x: Int, y: Int) = Triple(x+1, y, this[x+1, y])

    fun left(p: Pair<Int, Int>) = left(p.first, p.second)
    fun right(p: Pair<Int, Int>) = right(p.first, p.second)
    fun up(p: Pair<Int, Int>) = up(p.first, p.second)
    fun down(p: Pair<Int, Int>) = down(p.first, p.second)

    fun neighbors(original: Set<Pair<Int, Int>>): Map<Pair<Int,Int>, T?> =
        original.flatMap {
            ((it.first - 1)..(it.first + 1)).flatMap { x ->
                ((it.second -1 )..(it.second + 1)).map { y ->
                    Pair(x,y)
                }
            }
        }.filterNot {
            original.contains(it)
        }.associateWith {
            k -> this[k.first, k.second]
        }

    fun neighbors(x: Int, y: Int): Map<Pair<Int,Int>, T?> = neighbors(setOf(Pair(x, y)))
    fun neighbors(x: IntRange, y: Int): Map<Pair<Int, Int>, T?> =
        neighbors(x.map { Pair(it, y) }.toHashSet())
    fun neighbors(x: Int, y: IntRange): Map<Pair<Int, Int>, T?> =
        neighbors(y.map { Pair(x, it) }.toHashSet())
    fun neighbors(x: IntRange, y: IntRange): Map<Pair<Int, Int>, T?> =
        neighbors(x.flatMap { i -> y.map { j -> Pair(i,j) } }.toHashSet())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grid<*>) return false

        if (cells != other.cells) return false

        return true
    }

    override fun hashCode(): Int {
        return cells.hashCode()
    }


}

/**
 * Utility class that represents a 2D grid of values each which is represented by a single character
 */
open class StandardGrid<T: GridCell>(
    private val blank: Char = '.'
): Grid<T>() {
    fun pretty(): String {
        return (minX..maxX).map {x ->
            (minY..maxY).map {y ->
                this[x,y]?.symbol ?: blank
            }.joinToString("")
        }.joinToString("\n")
    }

    companion object {
        /**
         * Helper function that builds a grid from a string and mapping function
         */
        fun <T: GridCell> build(input: String, mapper: Function<Char, T?>): StandardGrid<T> {
            val grid = StandardGrid<T>()
            for ((x, row) in input.lines().withIndex()) {
                for ((y, v) in row.withIndex()) {
                    try {
                        grid[x, y] = mapper.apply(v)
                    } catch (_: IllegalArgumentException) {
                        // swallow IllegalArgumentException to support Enum::valueOf as a mapper
                    }
                }
            }
            return grid
        }

        /**
         * Builder variation that takes a String mapper function but still passes single characters. Provided
         * primarily to make it easy to use Enums as the GridCell type.
         */
        fun <T: GridCell> buildWithStrings(input: String, mapper: Function<String, T?>): StandardGrid<T> =
            build(input) {mapper.apply(String(charArrayOf(it)))}
    }
}
package com.staricka.adventofcode2023.util

import java.lang.StringBuilder
import java.util.function.Function
import kotlin.math.max

/**
 * Utility class that represents a 2D grid of values
 */
open class Grid<T> {
    protected val cells = HashMap<Int, HashMap<Int, T?>>()

    operator fun get(x: Int, y: Int): T? = cells[x]?.get(y)
    operator fun set(x: Int, y: Int, value: T?) {
        cells.computeIfAbsent(x){HashMap()}[y] = value
    }
    fun remove(x: Int, y: Int) {
        cells[x]?.remove(y)
        if (cells[x]?.isEmpty() == true) {
            cells.remove(x)
        }
    }

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
}

/**
 * Utility class that represents a 2D grid of values each which is represented by a single character
 */
open class StandardGrid<T: GridCell>(
    private val blank: Char = '.'
): Grid<T>() {
    fun pretty(): String {
        val minX = cells.keys.min()
        val maxX = cells.keys.max()
        val minY = cells.values.flatMap { it.keys }.min()
        val maxY = cells.values.flatMap { it.keys }.max()
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
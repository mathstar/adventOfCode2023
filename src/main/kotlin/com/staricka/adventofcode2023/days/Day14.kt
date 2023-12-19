package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid

enum class Rock(override val symbol: Char): GridCell {
    ROUND('O'), SQUARE('#'), BLANK('.');

    companion object {
        fun fromChar(c: Char): Rock {
            return when(c) {
                'O' -> ROUND
                '#' -> SQUARE
                '.' -> BLANK
                else -> throw Exception()
            }
        }
    }
}

fun Grid<Rock>.rollNorth() {
    for (x in minX..maxX) {
        for (y in minY..maxY) {
            if (this[x,y] == Rock.ROUND) {
                var dest = x
                while (dest > 0 && this[dest-1,y] == Rock.BLANK) dest--
                this[x,y] = Rock.BLANK
                this[dest,y] = Rock.ROUND
            }
        }
    }
}

fun Grid<Rock>.rollEast() {
    for (y in (minY..maxY).reversed()) {
        for (x in minX..maxX) {
            if (this[x,y] == Rock.ROUND) {
                var dest = y
                while (dest < maxY && this[x,dest+1] == Rock.BLANK) dest++
                this[x,y] = Rock.BLANK
                this[x,dest] = Rock.ROUND
            }
        }
    }
}

fun Grid<Rock>.rollSouth() {
    for (x in (minX..maxX).reversed()) {
        for (y in minY..maxY) {
            if (this[x,y] == Rock.ROUND) {
                var dest = x
                while (dest < maxX && this[dest+1,y] == Rock.BLANK) dest++
                this[x,y] = Rock.BLANK
                this[dest,y] = Rock.ROUND
            }
        }
    }
}

fun Grid<Rock>.rollWest() {
    for (y in minY..maxY) {
        for (x in minX..maxX) {
            if (this[x,y] == Rock.ROUND) {
                var dest = y
                while (dest > 0 && this[x,dest-1] == Rock.BLANK) dest--
                this[x,y] = Rock.BLANK
                this[x,dest] = Rock.ROUND
            }
        }
    }
}

enum class RollDirection {
    NORTH, WEST, SOUTH, EAST;

    fun next(): RollDirection {
        return entries[(ordinal + 1) % entries.size]
    }

    fun rollGrid(grid: Grid<Rock>) {
        when(this) {
            NORTH -> grid.rollNorth()
            WEST -> grid.rollWest()
            SOUTH -> grid.rollSouth()
            EAST -> grid.rollEast()
        }
    }
}

fun Grid<Rock>.load(): Int {
    return cells().filter { (_,_,v) -> v == Rock.ROUND }.sumOf { (x,_,_) -> maxX - x + 1 }
}

class Day14: Day {


    override fun part1(input: String): Int {
        val grid = StandardGrid.build(input, Rock::fromChar)
        grid.rollNorth()
        return grid.load()
    }

    override fun part2(input: String): Int {
        val grid = StandardGrid.build(input, Rock::fromChar)

        val history = LinkedHashMap<Pair<Grid<Rock>, RollDirection>, Long>()
        val totalRotations = 1000000000L * 4 - 1

        var loopStart = -1L
        var direction = RollDirection.entries.last()
        for (rollCount in 0..totalRotations) {
            direction = direction.next()
            direction.rollGrid(grid)

            val historyKey = Pair(grid.clone(), direction)
            if (history.containsKey(historyKey)) {
                loopStart = history[historyKey]!!
                break
            }
            history[historyKey] = rollCount
        }

        val loopLength = history.size - loopStart
        val index = ((totalRotations - loopStart) % loopLength + loopStart).toInt()

        val finalGrid = history.keys.toList()[index].first
        return finalGrid.load()
    }
}
package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.days.GardenCell.Companion.toGardenCell
import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid

enum class GardenCell(override val symbol: Char): GridCell {
    START('S'), PLOT('.'), ROCK('#');

    fun isPlot() = this == START || this == PLOT

    companion object {
        fun Char.toGardenCell(): GardenCell {
            return when (this) {
                'S' -> START
                '.' -> PLOT
                '#' -> ROCK
                else -> throw Exception()
            }
        }
    }
}

fun Grid<GardenCell>.reachable(numSteps: Int): Int {
    val (startX, startY, _) = cells().single { (_, _, v) -> v == GardenCell.START }
    var toProcess = setOf(Pair(startX, startY))

    for (i in 1..numSteps) {
        val next = HashSet<Pair<Int, Int>>()

        for (p in toProcess) {
            manhattanNeighbors(p).filter {(x,y,v) -> v?.isPlot() == true }
                .map { (x,y,_) -> Pair(x,y) }
                .forEach {
                    next += it
                }
        }

        toProcess = next
    }

    return toProcess.size
}

fun Grid<GardenCell>.reachableInfinite(numSteps: Int): Long {
    val (startX, startY, _) = cells().single { (_, _, v) -> v == GardenCell.START }
    var toProcess = setOf(Pair(startX, startY))

    for (i in 1..numSteps) {
        val next = HashSet<Pair<Int, Int>>()

        for (p in toProcess) {
            manhattanNeighbors(p)
                .filter { (x,y) ->
                    var xp = x % (maxX + 1)
                    var yp = y % (maxY + 1)
                    while (xp < 0) xp += maxX
                    while (yp < 0) yp += maxY
                    this[xp,yp]!!.isPlot()
                }.forEach { (x,y,_) -> next += Pair(x,y) }
        }

        toProcess = next
    }

    return toProcess.size.toLong()
}

class Day21(private val numSteps: Int = 64, private val numStepsPart2: Int = 26501365): Day {
    override fun part1(input: String): Int {
        return StandardGrid.build(input) {it.toGardenCell()}.reachable(numSteps)
    }

    override fun part2(input: String): Any? {
        return StandardGrid.build(input) {it.toGardenCell()}.reachableInfinite(numStepsPart2)
    }
}
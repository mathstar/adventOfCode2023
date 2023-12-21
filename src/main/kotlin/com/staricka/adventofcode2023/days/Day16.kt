package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid
import java.util.LinkedList
import kotlin.math.max

class MirrorGridTile(private val type: Char): GridCell {
    private val visited = HashSet<BeamDirection>()
    override val symbol = type

    fun energized() = visited.isNotEmpty()

    fun reset() = visited.clear()

    fun processBeam(direction: BeamDirection): List<BeamDirection> {
        if (visited.contains(direction)) return emptyList()
        visited.add(direction)
        return when (type) {
            '.' -> listOf(direction)
            '|' -> if (direction in listOf(BeamDirection.LEFT, BeamDirection.RIGHT)) listOf(BeamDirection.UP, BeamDirection.DOWN)
                   else listOf(direction)
            '-' -> if (direction in listOf(BeamDirection.UP, BeamDirection.DOWN)) listOf(BeamDirection.LEFT, BeamDirection.RIGHT)
                   else listOf(direction)
            '\\'-> when (direction) {
                BeamDirection.UP -> listOf(BeamDirection.LEFT)
                BeamDirection.RIGHT -> listOf(BeamDirection.DOWN)
                BeamDirection.DOWN -> listOf(BeamDirection.RIGHT)
                BeamDirection.LEFT -> listOf(BeamDirection.UP)
            }
            '/' -> when (direction) {
                BeamDirection.UP -> listOf(BeamDirection.RIGHT)
                BeamDirection.RIGHT -> listOf(BeamDirection.UP)
                BeamDirection.DOWN -> listOf(BeamDirection.LEFT)
                BeamDirection.LEFT -> listOf(BeamDirection.DOWN)
            }
            else -> throw Exception()
        }
    }
}

enum class BeamDirection {
    RIGHT, DOWN, LEFT, UP
}

data class Beam(val direction: BeamDirection, val x: Int, val y: Int) {
    fun translate(newDirection: BeamDirection): Beam {
        val newX = when(newDirection) {
            BeamDirection.UP -> x - 1
            BeamDirection.DOWN -> x + 1
            else -> x
        }
        val newY = when(newDirection) {
            BeamDirection.LEFT -> y - 1
            BeamDirection.RIGHT -> y + 1
            else -> y
        }
        return Beam(newDirection, newX, newY)
    }
}

fun Grid<MirrorGridTile>.energizedFromStart(direction: BeamDirection = BeamDirection.RIGHT, x: Int = 0, y: Int = 0): Int {
    val beams = LinkedList<Beam>()
    beams.add(Beam(direction, x, y))

    while (beams.isNotEmpty()) {
        val beam = beams.pop()
        this[beam.x, beam.y]!!.processBeam(beam.direction)
            .map { beam.translate(it) }
            .filter { it.x in minX..maxX && it.y in minY..maxY }
            .forEach { beams.add(it) }
    }

    return cells().count { (_,_,t) -> t.energized() }
}

fun Grid<MirrorGridTile>.reset() {
    cells().forEach { (_,_,v) -> v.reset() }
}

class Day16: Day {
    override fun part1(input: String): Int {
        val grid = StandardGrid.build(input, ::MirrorGridTile)
        return grid.energizedFromStart()
    }

    override fun part2(input: String): Int {
        val grid = StandardGrid.build(input, ::MirrorGridTile)
        var max = 0

        for (y in grid.minY..grid.maxY) {
            grid.reset()
            max = max(max, grid.energizedFromStart(BeamDirection.DOWN, 0, y))
            grid.reset()
            max = max(max, grid.energizedFromStart(BeamDirection.UP, grid.maxX, y))
        }

        for (x in grid.minX..grid.maxX) {
            grid.reset()
            max = max(max, grid.energizedFromStart(BeamDirection.RIGHT, x, 0))
            grid.reset()
            max = max(max, grid.energizedFromStart(BeamDirection.LEFT, x, grid.maxY))
        }

        return max
    }
}
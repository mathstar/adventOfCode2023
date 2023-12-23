package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.days.DigInstruction.Companion.toDigInstruction
import com.staricka.adventofcode2023.days.DigInstruction.Companion.toDigInstructionFromHex
import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.BasicCell
import com.staricka.adventofcode2023.util.Direction
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.StandardGrid
import java.util.LinkedList
import kotlin.math.abs

val digInstructionRegex = Regex("([LRUD]) ([0-9]+) \\(#([0-9a-f]+)\\)")
data class DigInstruction(val direction: Direction, val steps: Int, val hexCode: String) {
    companion object {
        fun String.toDigInstruction(): DigInstruction {
            val match = digInstructionRegex.matchEntire(this)
            return DigInstruction(
                when(match!!.groups[1]!!.value) {
                    "L" -> Direction.LEFT
                    "R" -> Direction.RIGHT
                    "U" -> Direction.UP
                    "D" -> Direction.DOWN
                    else -> throw Exception()
                },
                match.groups[2]!!.value.toInt(),
                match.groups[3]!!.value
            )
        }

        fun String.toDigInstructionFromHex(): DigInstruction {
            val hex = digInstructionRegex.matchEntire(this)!!.groups[3]!!.value
            return DigInstruction(
                when(hex[5]) {
                    '0' -> Direction.RIGHT
                    '1' -> Direction.DOWN
                    '2' -> Direction.LEFT
                    '3' -> Direction.UP
                    else -> throw Exception()
                },
                hex.substring(0, 5).toInt(16),
                ""
            )
        }
    }
}

fun Grid<BasicCell>.dig(digInstructions: List<DigInstruction>) {
    var x = 0
    var y = 0

    for (digInstruction in digInstructions) {
        (1..digInstruction.steps).forEach { _ ->
            val step = when(digInstruction.direction) {
                Direction.UP -> up(x, y)
                Direction.DOWN -> down(x, y)
                Direction.LEFT -> left(x, y)
                Direction.RIGHT -> right(x, y)
            }
            x = step.first
            y = step.second
            this[x,y] = BasicCell('#')
        }
    }
}

fun StandardGrid<BasicCell>.reachableFromOutside(): Int {
    val queue = LinkedList<Pair<Int, Int>>()
    var count = 0
    for (x in minX..maxX) {
        if (this[x, minY] == null) {
            queue.add(Pair(x, minY))
            count++
            this[x, minY] = BasicCell('0')
        }

        if (this[x, maxY] == null) {
            queue.add(Pair(x, maxY))
            count++
            this[x, maxY] = BasicCell('0')
        }
    }

    for (y in minY..maxY) {
        if (this[minX, y] == null) {
            queue.add(Pair(minX, y))
            count++
            this[minX, y] = BasicCell('0')
        }

        if (this[maxX, y] == null) {
            queue.add(Pair(maxX, y))
            count++
            this[maxX, y] = BasicCell('0')
        }
    }

    while (queue.isNotEmpty()) {
        val p = queue.pop()

        listOf(up(p), left(p), right(p), down(p)).filter { (x,y,v) ->
            x in minX..maxX && y in minY..maxY && v == null
        }.forEach { (x,y, _) ->
            queue.add(Pair(x,y))
            count++
            this[x,y] = BasicCell('0')
        }
    }

    return count
}

class Day18: Day {
    override fun part1(input: String): Int {
        val instructions = input.lines().filter { it.isNotBlank() }.map { it.toDigInstruction() }
        val grid = StandardGrid<BasicCell>()
        grid[0,0] = BasicCell('#')
        grid.dig(instructions)
        val reachableFromOutside = grid.reachableFromOutside()

        return grid.size() - reachableFromOutside
    }

    override fun part2(input: String): Long {
        val instructions = input.lines().filter { it.isNotBlank() }.map { it.toDigInstructionFromHex() }
        val x = ArrayList<Long>()
        val y = ArrayList<Long>()
        x += 0
        y += 0
        for (instruction in instructions) {
            when(instruction.direction) {
                Direction.UP -> {y += y.last() + instruction.steps; x += x.last()}
                Direction.DOWN -> {y += y.last() -  instruction.steps; x += x.last()}
                Direction.LEFT -> {x += x.last() - instruction.steps; y += y.last()}
                Direction.RIGHT -> {x += x.last() + instruction.steps; y += y.last()}
            }
        }

        var area = 0L
        var boundary = 0L
        for (i in x.indices) {
            if (i == x.size - 1) break
            area += (x[i] * y[i+1]) - (x[i+1] * y[i])
            boundary += if (x[i] == x[i+1]) abs(y[i] - y[i+1]) else abs(x[i] - x[i+1])
        }
        area = abs(area / 2) + boundary / 2 + 1

        return area
    }
}
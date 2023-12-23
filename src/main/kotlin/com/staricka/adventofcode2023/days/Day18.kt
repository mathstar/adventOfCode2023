package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.days.DigInstruction.Companion.toDigInstruction
import com.staricka.adventofcode2023.days.DigInstruction.Companion.toDigInstructionFromHex
import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.Direction
import kotlin.math.abs

val digInstructionRegex = Regex("([LRUD]) ([0-9]+) \\(#([0-9a-f]+)\\)")
data class DigInstruction(val direction: Direction, val steps: Int) {
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
                match.groups[2]!!.value.toInt()
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
                hex.substring(0, 5).toInt(16)
            )
        }
    }
}

class Day18: Day {
    override fun part1(input: String): Long {
        val instructions = input.lines().filter { it.isNotBlank() }.map { it.toDigInstruction() }
        return calculateArea(instructions)
    }

    override fun part2(input: String): Long {
        val instructions = input.lines().filter { it.isNotBlank() }.map { it.toDigInstructionFromHex() }
        return calculateArea(instructions)
    }

    private fun calculateArea(instructions: List<DigInstruction>): Long {
        val x = ArrayList<Long>()
        val y = ArrayList<Long>()
        x += 0
        y += 0
        for (instruction in instructions) {
            when (instruction.direction) {
                Direction.UP -> {
                    y += y.last() + instruction.steps; x += x.last()
                }

                Direction.DOWN -> {
                    y += y.last() - instruction.steps; x += x.last()
                }

                Direction.LEFT -> {
                    x += x.last() - instruction.steps; y += y.last()
                }

                Direction.RIGHT -> {
                    x += x.last() + instruction.steps; y += y.last()
                }
            }
        }

        var area = 0L
        var boundary = 0L
        for (i in x.indices) {
            if (i == x.size - 1) break
            area += (x[i] * y[i + 1]) - (x[i + 1] * y[i])
            boundary += if (x[i] == x[i + 1]) abs(y[i] - y[i + 1]) else abs(x[i] - x[i + 1])
        }
        area = abs(area / 2) + boundary / 2 + 1

        return area
    }
}
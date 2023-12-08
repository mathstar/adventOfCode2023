package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.lcm

class Day8: Day {
    enum class Direction {
        L, R
    }

    data class MapPath(val left: String, val right: String) {
        companion object {
            fun fromString(input: String): Pair<String, MapPath> {
                val split = input.split(" = ")
                val source = split[0]
                val destSplit = split[1].split(", ")
                val left = Regex("[A-Z0-9]+").find(destSplit[0])!!.value
                val right = Regex("[A-Z0-9]+").find(destSplit[1])!!.value
                return Pair(source, MapPath(left, right))
            }
        }
    }

    private fun getDirectionSequence(input: String): List<Direction> {
        return input.toCharArray().map { Direction.valueOf(it.toString()) }
    }

    private fun getMapPaths(input: List<String>): Map<String, MapPath> {
        return input.filter { it.isNotBlank() }
            .associate { MapPath.fromString(it) }
    }

    override fun part1(input: String): Int {
        val lines = input.lines()
        val directionSequence = getDirectionSequence(lines[0])
        val mapPaths = getMapPaths(lines.subList(2, lines.size))

        var position = "AAA"
        var steps = 0
        var nextDirection = 0
        while (position != "ZZZ") {
            position = mapPaths[position]!!.let { when (directionSequence[nextDirection]) {
                Direction.L -> it.left
                Direction.R -> it.right
            } }
            steps++
            nextDirection = (nextDirection + 1) % directionSequence.size
        }

        return steps
    }

    private fun zPosition(start: String, mapPaths: Map<String, MapPath>, directionSequence: List<Direction>): Int {
        var position = start
        var steps = 0
        var nextDirection = 0

        while (!position.endsWith("Z")) {
            position = mapPaths[position]!!.let { when (directionSequence[nextDirection]) {
                Direction.L -> it.left
                Direction.R -> it.right
            } }
            steps++
            nextDirection = (nextDirection + 1) % directionSequence.size
        }
        return steps
    }


    override fun part2(input: String): Long {
        val lines = input.lines()
        val directionSequence = getDirectionSequence(lines[0])
        val mapPaths = getMapPaths(lines.subList(2, lines.size))

        val position = mapPaths.keys.filter { it.endsWith("A") }
        val zPosition = position.map { zPosition(it, mapPaths, directionSequence) }

        return zPosition.map { it.toLong() }.reduce { acc, i -> lcm(acc, i) }
    }
}
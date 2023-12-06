package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day

class Day6: Day {
    data class Race(val time: Long, val distance: Long) {
        fun waysToWin(): Long {
            var result = 0L
            for (accel in (0..time)) {
                if (accel * (time - accel) > distance) result++
            }
            return result
        }
    }

    private fun parseRaces(input: String): List<Race> {
        val times = input.lines()[0].split(":")[1].trim().split(Regex(" +"))
        val distances = input.lines()[1].split(":")[1].trim().split(Regex(" +"))
        return times.indices.map { Race(times[it].toLong(), distances[it].toLong()) }
    }

    private fun parseRace(input: String): Race {
        val time = input.lines()[0].split(":")[1].replace(Regex(" +"), "").toLong()
        val distance = input.lines()[1].split(":")[1].replace(Regex(" +"), "").toLong()
        return Race(time, distance)
    }

    override fun part1(input: String): Long {
        return parseRaces(input).map { it.waysToWin() }.reduce { acc, i -> acc * i }
    }

    override fun part2(input: String): Long {
        return parseRace(input).waysToWin()
    }
}
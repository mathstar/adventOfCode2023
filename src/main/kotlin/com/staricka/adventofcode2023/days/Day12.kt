package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day

class Day12: Day {
    enum class Condition {
        OPERATIONAL, BROKEN, UNKNOWN;

        companion object {
            fun fromChar(c: Char): Condition {
                return when (c) {
                    '.' -> OPERATIONAL
                    '#' -> BROKEN
                    '?' -> UNKNOWN
                    else -> throw Exception()
                }
            }
        }
    }

    data class Row(val conditions: List<Condition>, val groups: List<Int>) {
        fun popCondition(): Pair<Condition, Row> {
            return Pair(conditions.first(), Row(conditions.subList(1, conditions.size), groups))
        }

        fun popGroup(): Pair<Int, Row> {
            return Pair(groups.first(), Row(conditions, groups.subList(1, groups.size)))
        }

        fun unfold(): Row {
            val unfoldedConditions: List<Condition> = listOf(conditions, conditions, conditions, conditions, conditions)
                .reduce {acc, next -> acc + listOf(Condition.UNKNOWN) + next }
            val unfoldedGroups: List<Int> = listOf(groups, groups, groups, groups, groups).flatten()
            return Row(unfoldedConditions, unfoldedGroups)
        }

        companion object {
            fun fromString(line: String): Row {
                val conditions = line.split(" ")[0].toCharArray().map { Condition.fromChar(it) }
                val groups = line.split(" ")[1].split(",").map { it.toInt() }
                return Row(conditions, groups)
            }
        }
    }

    private fun possibleArrangements(row: Row): Long {
        return recurseArrangements(row, 0)
    }

    private val memos = HashMap<Pair<Row, Int>, Long>()

    private fun recurseArrangements(row: Row, brokenCount: Int): Long {
        return memos[Pair(row, brokenCount)] ?: recurseArrangementsBody(row, brokenCount).also { memos[Pair(row, brokenCount)] = it }
    }

    private fun recurseArrangementsBody(row: Row, brokenCount: Int): Long {
        if (row.conditions.isEmpty()) {
            return if (row.groups.isEmpty()) {
                if (brokenCount == 0) 1 else 0
            } else {
                if (row.groups.size == 1 && row.groups[0] == brokenCount) 1 else 0
            }
        }

        val (condition, next) = row.popCondition()
        when (condition) {
            Condition.OPERATIONAL -> {
                if (brokenCount == 0) {
                    return recurseArrangements(next, 0)
                }
                if (row.groups.isNotEmpty() && row.groups[0] == brokenCount) {
                    return recurseArrangements(next.popGroup().second, 0)
                }
                return 0
            }
            Condition.BROKEN -> {
                val nextBroken = brokenCount + 1
                if (row.groups.isNotEmpty() && row.groups[0] >= nextBroken) {
                    return recurseArrangements(next, nextBroken)
                }
                return 0
            }
            Condition.UNKNOWN -> {
                var result = 0L

                // BROKEN case
                if (row.groups.isNotEmpty() && row.groups[0] > brokenCount) {
                    result += recurseArrangements(next, brokenCount + 1)
                }

                // OPERATIONAL case
                if (brokenCount == 0) {
                    result += recurseArrangements(next, 0)
                } else if (row.groups.isNotEmpty() && row.groups[0] == brokenCount) {
                    result += recurseArrangements(next.popGroup().second, 0)
                }
                return result
            }
        }
    }

    override fun part1(input: String): Long {
        return input.lines().filter { it.isNotBlank() }.map { Row.fromString(it) }.sumOf { possibleArrangements(it) }
    }

    override fun part2(input: String): Long {
        val rows = input.lines().filter { it.isNotBlank() }.map { Row.fromString(it).unfold() }
        return rows.parallelStream().mapToLong { possibleArrangements(it) }.sum()
    }
}
package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import java.util.regex.Pattern
import kotlin.math.pow

class Day4: Day {
    data class Card(val winning:List<Int>, val have:List<Int>) {
        fun points(): Int {
            return 2.0.pow(winning() - 1).toInt()
        }

        fun winning(): Int {
            return have.count { winning.contains(it) }
        }

        companion object {
            private val spaces = Pattern.compile(" +")

            fun fromString(input: String): Card {
                val nums = input.split(":")[1]
                val winning = nums.split("|")[0].trim().split(spaces).map { it.toInt() }
                val have = nums.split("|")[1].trim().split(spaces).map { it.toInt() }
                return Card(winning, have)
            }
        }
    }

    override fun part1(input: String): Int {
        return input.lines().filter { it.isNotBlank() }
            .map { Card.fromString(it) }
            .sumOf { it.points() }
    }

    override fun part2(input: String): Int {
        val cards = input.lines().filter { it.isNotBlank() }.map { Card.fromString(it) }
        val counts = IntArray(cards.size){1}
        for ((c, card) in cards.withIndex()) {
            for (i in 1..card.winning()) {
                if (c + i < cards.size) {
                    counts[c + i] += counts[c]
                }
            }
        }
        return counts.sum()
    }
}
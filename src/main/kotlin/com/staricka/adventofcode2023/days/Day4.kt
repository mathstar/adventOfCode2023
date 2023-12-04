package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import kotlin.math.pow

class Day4: Day {
    data class Card(val winning:List<Int>, val have:List<Int>) {
        fun points(): Int {
            return 2.0.pow(have.filter { winning.contains(it) }.count() - 1).toInt()
        }

        fun winning(): Int {
            return have.filter { winning.contains(it) }.count()
        }

        companion object {
            fun fromString(input: String): Card {
                val nums = input.split(":")[1]
                val winning = nums.split("|")[0].trim().split(" ").filter { it.isNotBlank() }.map { it.toInt() }
                val have = nums.split("|")[1].trim().split(" ").filter { it.isNotBlank() }.map { it.toInt() }
                return Card(winning, have)
            }
        }
    }

    override fun part1(input: String): Any? {
        return input.lines().filter { it.isNotBlank() }
            .map { Card.fromString(it) }
            .map { it.points() }
            .sum()
    }

    override fun part2(input: String): Any? {
        val cards = input.lines().filter { it.isNotBlank() }.map { Card.fromString(it) }
        var sum = cards.size
        val counts = IntArray(cards.size)
        for (i in cards.indices) counts[i] = 1
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
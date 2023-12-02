package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import java.util.regex.Pattern
import kotlin.math.max

class Day2: Day {
    data class Game(val id: Int, val reveals: List<Reveal>) {
        fun possible(counts: Map<String, Int>): Boolean =
            reveals.all { it.possible(counts) }

        fun minCounts(): Map<String, Int> {
            val result = HashMap<String, Int>()
            reveals.map(Reveal::minCounts).flatMap { it.entries }.forEach {(k,v) ->
                result.compute(k) {_,existing -> max(v, existing ?: 0) }
            }
            return result
        }

        fun power(): Int {
            return minCounts().values.reduce { acc, i ->  acc * i}
        }

        companion object {
            val gamePattern = Pattern.compile(
                "Game (?<id>[0-9]+): (?<reveals>((?<reveal>((?<element>(?<count>[0-9]+) (?<color>[a-z]+)),? ?)+);? ?)+)"
            )

            fun fromString(input: String): Game {
                val matcher = gamePattern.matcher(input)
                matcher.find()
                return Game(
                    matcher.group("id").toInt(),
                    matcher.group("reveals").split(";").map(Reveal::fromString)
                )
            }
        }
    }

    data class Reveal(val elements: List<Element>) {
        fun possible(counts: Map<String, Int>): Boolean =
            elements.all { it.possible(counts) }

        fun minCounts(): Map<String, Int> = elements.associate { it.color to it.count }

        companion object {
            val revealPattern = Pattern.compile(
                "(?<reveal>((?<element>(?<count>[0-9]+) (?<color>[a-z]+)),? ?)+)"
            )

            fun fromString(input: String): Reveal {
                val matcher = revealPattern.matcher(input)
                matcher.find()
                return Reveal(matcher.group().split(",").map(Element::fromString))
            }
        }
    }

    data class Element(val count: Int, val color: String) {
        fun possible(counts: Map<String, Int>): Boolean =
            (counts[color]?: 0) >= count

        companion object {
            val elementPattern = Pattern.compile(
                "(?<element>(?<count>[0-9]+) (?<color>[a-z]+))"
            )

            fun fromString(input: String): Element {
                val matcher = elementPattern.matcher(input)
                matcher.find()
                return Element(matcher.group("count").toInt(), matcher.group("color"))
            }
        }
    }

    override fun part1(input: String): Int {
        return input.lines().filter(String::isNotBlank)
            .map(Game::fromString)
            .filter{
                it.possible(mapOf(
                    "red" to 12,
                    "green" to 13,
                    "blue" to 14
                ))
            }.sumOf { it.id }
    }

    override fun part2(input: String): Any? {
        return input.lines().filter(String::isNotBlank)
            .map(Game::fromString)
            .sumOf(Game::power)
    }
}
package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day21Test {
    val day = Day21(6)

    @Test
    fun part1() {
        assertEquals(16, day.part1("""
            ...........
            .....###.#.
            .###.##..#.
            ..#.#...#..
            ....#.#....
            .##..S####.
            .##..#...#.
            .......##..
            .##.#.####.
            .##..##.##.
            ...........
        """.trimIndent()))
    }

    @Test
    fun part2() {
        val testValues = mapOf(
            6 to 16L,
            10 to 50L,
            50 to 1594L,
            100 to 6536L,
            500 to 167004L,
            1000 to 668697L,
            5000 to 16733044L
        )
        testValues.forEach{(numSteps, reachable) ->
            assertEquals(reachable, Day21(numStepsPart2 = numSteps).part2("""
                ...........
                .....###.#.
                .###.##..#.
                ..#.#...#..
                ....#.#....
                .##..S####.
                .##..#...#.
                .......##..
                .##.#.####.
                .##..##.##.
                ...........
            """.trimIndent()))
        }
    }
}
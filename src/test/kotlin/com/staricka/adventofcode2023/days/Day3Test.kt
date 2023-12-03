package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day3Test {
    private val day = Day3()

    @Test
    fun part1() {
        assertEquals(4361, day.part1("""
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...${'$'}.*....
            .664.598..
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(467835, day.part2("""
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...${'$'}.*....
            .664.598..
        """.trimIndent()))
    }
}
package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day6Test {
    private val day = Day6()

    @Test
    fun part1() {
        assertEquals(288, day.part1("""
            Time:      7  15   30
            Distance:  9  40  200
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(71503, day.part2("""
            Time:      7  15   30
            Distance:  9  40  200
        """.trimIndent()))
    }
}
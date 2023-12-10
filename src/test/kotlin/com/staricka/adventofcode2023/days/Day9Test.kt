package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day9Test {
    private val day = Day9()

    @Test
    fun part1() {
        assertEquals(114, day.part1("""
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(2, day.part2("""
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
        """.trimIndent()))
    }
}
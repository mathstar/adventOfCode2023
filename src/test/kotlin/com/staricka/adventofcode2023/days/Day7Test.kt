package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day7Test {
    private val day = Day7()

    @Test
    fun part1() {
        assertEquals(6440, day.part1("""
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(5905, day.part2("""
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
        """.trimIndent()))
    }
}
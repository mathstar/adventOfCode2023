package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class Day1Test {
    private val day = Day1()

    @Test
    fun part1() {
        assertEquals(142, day.part1("""
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(281, day.part2("""
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()))
    }
}
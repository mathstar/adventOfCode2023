package com.staricka.adventofcode2023.days

import kotlin.test.Test
import kotlin.test.assertEquals

class Day16Test {
    val day = Day16()

    @Test
    fun part1() {
        assertEquals(46, day.part1("""
            .|...\....
            |.-.\.....
            .....|-...
            ........|.
            ..........
            .........\
            ..../.\\..
            .-.-/..|..
            .|....-|.\
            ..//.|....
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(51, day.part2("""
            .|...\....
            |.-.\.....
            .....|-...
            ........|.
            ..........
            .........\
            ..../.\\..
            .-.-/..|..
            .|....-|.\
            ..//.|....
        """.trimIndent()))
    }
}
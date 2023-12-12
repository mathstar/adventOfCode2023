package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10Test {
    private val day = Day10()

    @Test
    fun part1() {
        assertEquals(4, day.part1("""
            .....
            .S-7.
            .|.|.
            .L-J.
            .....
        """.trimIndent()))
        assertEquals(8, day.part1("""
            ..F7.
            .FJ|.
            SJ.L7
            |F--J
            LJ...
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(4, day.part2("""
            ...........
            .S-------7.
            .|F-----7|.
            .||.....||.
            .||.....||.
            .|L-7.F-J|.
            .|..|.|..|.
            .L--J.L--J.
            ...........
        """.trimIndent()))

        assertEquals(8, day.part2("""
            .F----7F7F7F7F-7....
            .|F--7||||||||FJ....
            .||.FJ||||||||L7....
            FJL7L7LJLJ||LJ.L-7..
            L--J.L7...LJS7F-7L7.
            ....F-J..F7FJ|L7L7L7
            ....L7.F7||L7|.L7L7|
            .....|FJLJ|FJ|F7|.LJ
            ....FJL-7.||.||||...
            ....L---J.LJ.LJLJ...
        """.trimIndent()))

        assertEquals(10, day.part2("""
            FF7FSF7F7F7F7F7F---7
            L|LJ||||||||||||F--J
            FL-7LJLJ||||||LJL-77
            F--JF--7||LJLJ7F7FJ-
            L---JF-JLJ.||-FJLJJ7
            |F|F-JF---7F7-L7L|7|
            |FFJF7L7F-JF7|JL---7
            7-L-JL7||F7|L7F-7F7|
            L.L7LFJ|||||FJL7||LJ
            L7JLJL-JLJLJL--JLJ.L
        """.trimIndent()))
    }
}
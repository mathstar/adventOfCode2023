package com.staricka.adventofcode2023.days

import kotlin.test.Test
import kotlin.test.assertEquals

class Day17Test {
    val day = Day17()

    @Test
    fun part1() {
        assertEquals(102, day.part1("""
            2413432311323
            3215453535623
            3255245654254
            3446585845452
            4546657867536
            1438598798454
            4457876987766
            3637877979653
            4654967986887
            4564679986453
            1224686865563
            2546548887735
            4322674655533
        """.trimIndent()))

        assertEquals(9, day.part1("""
            11199
            12199
            99199
            99131
            99111
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(94, day.part2("""
            2413432311323
            3215453535623
            3255245654254
            3446585845452
            4546657867536
            1438598798454
            4457876987766
            3637877979653
            4654967986887
            4564679986453
            1224686865563
            2546548887735
            4322674655533
        """.trimIndent()))

        assertEquals(71, day.part2("""
            111111111111
            999999999991
            999999999991
            999999999991
            999999999991
        """.trimIndent()))
    }
}
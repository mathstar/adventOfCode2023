package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day12Test {
    val day = Day12()

    @Test
    fun part1() {
        assertEquals(21L, day.part1("""
            ???.### 1,1,3
            .??..??...?##. 1,1,3
            ?#?#?#?#?#?#?#? 1,3,1,6
            ????.#...#... 4,1,1
            ????.######..#####. 1,6,5
            ?###???????? 3,2,1
        """.trimIndent()))
    }

    @Test
    fun part2() {
        assertEquals(525152L, day.part2("""
            ???.### 1,1,3
            .??..??...?##. 1,1,3
            ?#?#?#?#?#?#?#? 1,3,1,6
            ????.#...#... 4,1,1
            ????.######..#####. 1,6,5
            ?###???????? 3,2,1
        """.trimIndent()))
    }
}
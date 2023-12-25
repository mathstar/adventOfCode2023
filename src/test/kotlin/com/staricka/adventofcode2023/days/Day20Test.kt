package com.staricka.adventofcode2023.days

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day20Test {
    val day = Day20()

    @Test
    fun part1() {
        assertEquals(32000000L, day.part1("""
            broadcaster -> a, b, c
            %a -> b
            %b -> c
            %c -> inv
            &inv -> a
        """.trimIndent()))

        assertEquals(11687500L, day.part1("""
            broadcaster -> a
            %a -> inv, con
            &inv -> b
            %b -> con
            &con -> output
        """.trimIndent()))
    }
}
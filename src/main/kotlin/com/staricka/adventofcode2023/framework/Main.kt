package com.staricka.adventofcode2023.framework

import kotlin.reflect.full.createInstance

fun main() {
    val input = System.getenv("DAY") ?: run {
        println("Run which day?")
        readln()
    }

    if (input.isBlank()) {
        println("Invalid day")
        return
    }

    val part = when (input[input.length -1]) {
        'a' -> DayPart.PART1
        'b' -> DayPart.PART2
        else -> DayPart.BOTH
    }

    val dayNumber = if (part == DayPart.BOTH) input.toIntOrNull() else input.substring(0..input.length-2).toIntOrNull()

    if (dayNumber == null) {
        println("Invalid day")
        return
    }

    try {
        val day = Class.forName("com.staricka.adventofcode2023.days.Day$dayNumber")?.kotlin

        if (day == null) {
            println("Invalid day")
            return
        }

        (day.createInstance() as Day).run(part)
    } catch (_: ClassNotFoundException) {
        println("Invalid day")
    }
}
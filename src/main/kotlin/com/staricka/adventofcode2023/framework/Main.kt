package com.staricka.adventofcode2023.framework

import kotlin.reflect.full.createInstance
import kotlin.system.measureTimeMillis

fun instantiateDay(dayNumber: Int): Day? {
    val day = Class.forName("com.staricka.adventofcode2023.days.Day$dayNumber")?.kotlin
    return day?.createInstance() as Day
}

fun benchmark() {
    var total = 0L
    for (d in 1..25) {
        val day = instantiateDay(d)
        if (day != null) {
            val input = day.inputProvider.getInput(d)
            val part1 = measureTimeMillis { day.part1(input) }
            val part2 = measureTimeMillis { day.part2(input) }
            println("Day $d: ${part1}ms + ${part2}ms = ${part1 + part2}ms")
            total += part1 + part2
        }
    }
    println("Total: ${total}ms")
}

fun main() {
    val input = System.getenv("DAY") ?: run {
        println("Run which day?")
        readln()
    }

    if (input.isBlank()) {
        println("Invalid day")
        return
    }

    if (input == "BENCHMARK") {
        benchmark()
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
        val day = instantiateDay(dayNumber)

        if (day == null) {
            println("Invalid day")
            return
        }

        day.run(part)
    } catch (_: ClassNotFoundException) {
        println("Invalid day")
    }
}
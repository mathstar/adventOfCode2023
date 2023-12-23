package com.staricka.adventofcode2023.framework

import java.lang.Exception
import kotlin.system.measureTimeMillis

interface Day {
    val id: Int?
        get() = Regex("[0-9]+").find(javaClass.simpleName)?.groups?.get(0)?.value?.toIntOrNull()
    val inputProvider: InputDownloader
        get() = InputDownloader()

    fun part1(input: String): Any?
    fun part2(input: String): Any?

    fun run(part: DayPart = DayPart.BOTH) {
        val input = id?.let { inputProvider.getInput(it) } ?: throw Exception("Unexpected day class name")
        when (part) {
            DayPart.PART1 -> {
                var result: Any?
                val time = measureTimeMillis { result = part1(input) }
                println(result)
                println("Runtime: ${time}ms")
            }
            DayPart.PART2 -> {
                var result: Any?
                val time = measureTimeMillis { result = part2(input) }
                println(result)
                println("Runtime: ${time}ms")
            }
            DayPart.BOTH -> {
                var result: Any?
                val time1 = measureTimeMillis { result = part1(input) }
                println(result)
                val time2 = measureTimeMillis { result = part2(input) }
                println(result)
                println("Runtime: ${time1}ms + ${time2}ms = ${time1 + time2}ms")
            }
        }
    }
}

enum class DayPart {
    PART1, PART2, BOTH
}
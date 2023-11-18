package com.staricka.adventofcode2023.framework

import java.lang.Exception

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
            DayPart.PART1 -> println(part1(input))
            DayPart.PART2 -> println(part2(input))
            DayPart.BOTH -> {
                println(part1(input))
                println(part2(input))
            }
        }
    }
}

enum class DayPart {
    PART1, PART2, BOTH
}
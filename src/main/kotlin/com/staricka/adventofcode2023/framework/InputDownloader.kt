package com.staricka.adventofcode2023.framework

import java.io.File
import java.lang.Exception

class InputDownloader: InputProvider {
    override fun getInput(day: Int): String {
        getFromCache(day)
        return getFromCache(day) ?: downloadInput(day).also { cacheInput(day, it) }
    }

    fun getFromCache(day: Int): String? =
        try {
            File("cache/day$day.txt").readText()
        } catch (_: Exception) {
            null
        }

    fun cacheInput(day: Int, input: String) =
        try {
            File("cache/day$day.txt").writeText(input)
        } catch (_: Exception) {}

    fun downloadInput(day: Int): String =
        khttp.get(
            "https://adventofcode.com/2023/day/$day/input",
            mapOf(
                "UserAgent" to "https://github.com/mathstar/adventOfCode2023 by mstaricka@gmail.com",
                "Cookie" to "session=${System.getenv("SESSION")}"
            )
        ).text
}
package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.regex.Pattern
import kotlin.streams.toList

class Day1: Day {
    val p = Pattern.compile("[0-9]|(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)")
    override fun part1(input: String): Any? {
        var total = 0
        input.lines().map {
            if(!it.isBlank()) {
                var result = it.chars().toList().filter { it >= '0'.toInt() && it <= '9'.toInt() }.first() - '0'.toInt()
                result *= 10
                result += it.chars().toList().reversed().filter { it >= '0'.toInt() && it <= '9'.toInt() }.first() - '0'.toInt()
                total += result
            }
        }
        return total
    }

    fun parseNum(input: String): Int {
        try {
            return Integer.parseInt(input)
        } catch (e: NumberFormatException) {
            return when (input) {
                "one" -> 1
                "two" -> 2
                "three" -> 3
                "four" -> 4
                "five" -> 5
                "six" -> 6
                "seven" -> 7
                "eight" -> 8
                "nine" -> 9
                else -> throw Exception("invalid")
            }
        }
    }

    override fun part2(input: String): Any? {
        var total = 0
        input.lines().map {
            if(!it.isBlank()) {
                var results = it.indices.map { i -> p.matcher(it.substring(i)).results().findFirst() }.filter { it.isPresent }.map { it.get() }.toList()
                var result = parseNum(results.first().group()) * 10
                result += parseNum(results.last().group())
                total += result
            }
        }
        return total
    }
}
package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.regex.Pattern

class Day1: Day {
    private val numPattern = Pattern.compile("^[0-9]|(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)")
    override fun part1(input: String): Any {
        return input.lines().filter(String::isNotBlank).sumOf {
            val digits = it.toCharArray().filter(Char::isDigit).map { c -> Integer.parseInt(c.toString()) }
            digits.first() * 10 + digits.last()
        }
    }

    private fun parseNum(input: String): Int {
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

    override fun part2(input: String): Any {
        return input.lines().filter(String::isNotBlank).sumOf {
            val results = it.indices.map { i -> numPattern.matcher(it.substring(i)).results().findFirst() }
                .filter{ o -> o.isPresent}
                .map { o -> o.get().group() }
                .toList()
            parseNum(results.first()) * 10 + parseNum(results.last())
        }
    }
}
package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day

class Day9: Day {
    private fun produceDiffSequence(original: List<Int>): List<List<Int>> {
        val result = ArrayList<List<Int>>()
        result.add(original)
        while (result.last().any { it != 0 }) {
            result.add(
                result.last()
                    .indices
                    .filter { it != 0 }
                    .map { result.last()[it] - result.last()[it - 1] }
            )
        }

        return result
    }

    private fun extrapolate(sequences: List<List<Int>>): Int {
        return sequences.reversed()
            .fold(0) {acc, n -> acc + n.last()}
    }

    private fun extrapolateReverse(sequences: List<List<Int>>): Int {
        return sequences.reversed()
            .fold(0) {acc, n -> n.first() - acc}
    }

    override fun part1(input: String): Int {
        return input.lines()
            .filter { it.isNotBlank() }
            .map { it.split(" ").map {n -> n.toInt() } }
            .map { produceDiffSequence(it) }
            .sumOf { extrapolate(it) }
    }

    override fun part2(input: String): Int {
        return input.lines()
            .filter { it.isNotBlank() }
            .map { it.split(" ").map {n -> n.toInt() } }
            .map { produceDiffSequence(it) }
            .sumOf { extrapolateReverse(it) }
    }
}
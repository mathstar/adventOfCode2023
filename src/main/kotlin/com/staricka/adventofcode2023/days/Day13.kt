package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.*

enum class ReflectionDirection {
    COLUMN, ROW
}

data class ReflectionPoint(val num: Int, val direction: ReflectionDirection)

fun <T: GridCell> StandardGrid<T>.findReflection(): ReflectionPoint {
    for (reflection in minX until maxX) {
        for (i in 0..maxX) {
            val top = reflection - i
            val bottom = reflection + 1 + i

            if (top < 0 || bottom > maxX) return ReflectionPoint(reflection, ReflectionDirection.ROW)
            if (row(top).map { (_,_,v) -> v } != row(bottom).map { (_,_,v) -> v }) break
        }
    }

    for (reflection in minY until maxY) {
        for (i in 0..maxY) {
            val left = reflection - i
            val right = reflection + 1 + i

            if (left < 0 || right > maxY) return ReflectionPoint(reflection, ReflectionDirection.COLUMN)
            if (col(left).map { (_,_,v) -> v } != col(right).map { (_,_,v) -> v }) break
        }
    }

    throw Exception()
}

fun <T: GridCell> StandardGrid<T>.findSmudgeReflection(original: ReflectionPoint): ReflectionPoint {
    for (reflection in minX until maxX) {
        var smudgeIdentified = false
        reflectionInner@for (i in 0..maxX) {
            val top = reflection - i
            val bottom = reflection + 1 + i

            if (top < 0 || bottom > maxX) {
                val reflectionPoint = ReflectionPoint(reflection, ReflectionDirection.ROW)
                if (reflectionPoint != original) return reflectionPoint
                break@reflectionInner
            }
            val topRow = row(top)
            val bottomRow = row(bottom)
            for (j in 0..maxY) {
                if (topRow[j].third != bottomRow[j].third) {
                    if (!smudgeIdentified) {
                        smudgeIdentified = true
                    } else {
                        break@reflectionInner
                    }
                }
            }
        }
    }

    for (reflection in minY until maxY) {
        var smudgeIdentified = false
        reflectionInner@for (i in 0..maxY) {
            val left = reflection - i
            val right = reflection + 1 + i

            if (left < 0 || right > maxY) {
                val reflectionPoint = ReflectionPoint(reflection, ReflectionDirection.COLUMN)
                if (reflectionPoint != original) return reflectionPoint
                break@reflectionInner
            }
            val leftCol = col(left)
            val rightCol = col(right)
            for (j in 0..maxX) {
                if (leftCol[j].third != rightCol[j].third) {
                    if (!smudgeIdentified) {
                        smudgeIdentified = true
                    } else {
                        break@reflectionInner
                    }
                }
            }
        }
    }

    throw Exception()
}

class Day13: Day {
    override fun part1(input: String): Int {
        return input.splitByBlankLines().map { StandardGrid.build(it, ::BasicCell) }
            .map { it.findReflection() }
            .sumOf { if(it.direction == ReflectionDirection.COLUMN) it.num + 1 else 100 * (it.num + 1) }
    }

    override fun part2(input: String): Int {
        return input.splitByBlankLines().map { StandardGrid.build(it, ::BasicCell) }
            .map { Pair(it, it.findReflection()) }
            .map { (grid, reflection) -> grid.findSmudgeReflection(reflection) }
            .sumOf { if(it.direction == ReflectionDirection.COLUMN) it.num + 1 else 100 * (it.num + 1) }
    }
}
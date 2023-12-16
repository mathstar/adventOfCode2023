package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid
import kotlin.math.abs

class Day11(val oldMultiplier: Long = 1000000): Day {
    enum class Galaxy: GridCell {
        `#`;

        override val symbol = name[0]
    }

    private fun calculateDistances(grid: StandardGrid<Galaxy>, multiplier: Long): Long {
        val emptyRows = (grid.minX..grid.maxX).filter { grid.row(it).isEmpty() }.toSortedSet()
        val emptyCols = (grid.minY..grid.maxY).filter { grid.col(it).isEmpty() }.toSortedSet()

        val galaxies = grid.cells()
        var total = 0L
        for (i in galaxies.indices) {
            for (j in (i+1 until galaxies.size)) {
                val (x1,y1,_) = galaxies[i]
                val (x2,y2,_) = galaxies[j]

                val x1l = x1 + emptyRows.headSet(x1).size * (multiplier - 1)
                val y1l = y1 + emptyCols.headSet(y1).size * (multiplier - 1)
                val x2l = x2 + emptyRows.headSet(x2).size * (multiplier - 1)
                val y2l = y2 + emptyCols.headSet(y2).size * (multiplier - 1)

                total += abs(x1l - x2l) + abs(y1l - y2l)
            }
        }
        return total
    }

    override fun part1(input: String): Long {
        val grid = StandardGrid.buildWithStrings(input) {Galaxy.valueOf(it)}
        return calculateDistances(grid, 2)
    }

    override fun part2(input: String): Long {
        val grid = StandardGrid.buildWithStrings(input) {Galaxy.valueOf(it)}
        return calculateDistances(grid, oldMultiplier)
    }

}
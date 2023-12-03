package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.BasicCell
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.StandardGrid

class Day3: Day {
    /**
     * Given the start coordinate of a number, determines the number and the end coordinate
     * @return (number, end)
     */
    private fun identifyNumber(grid: Grid<BasicCell>, i: Int, j: Int): Pair<Int, Int> {
        var j = j
        var num = (grid[i, j]!!.symbol - '0')
        while (grid[i, j + 1]?.symbol?.isDigit() == true) {
            num *= 10
            num += grid[i, j + 1]!!.symbol - '0'
            j++
        }
        return Pair(num, j)
    }

    override fun part1(input: String): Int {
        var sum = 0
        val grid = StandardGrid.build(input){BasicCell(it)}
        for (i in 0..input.lines().filter(String::isNotBlank).size) {
            var j = 0
            while (j <= input.lines().first().length) {
                if (grid[i, j]?.symbol?.isDigit() == true) {
                    val rangeStart = j
                    var (num, rangeEnd) = identifyNumber(grid, i, j)
                    j = rangeEnd
                    for ((_,cell) in grid.neighbors(i, (rangeStart..rangeEnd))) {
                        if(cell?.symbol != null && !cell.symbol.isDigit() && cell.symbol != '.') {
                            sum += num
                            break
                        }
                    }
                }
                j++
            }
        }
        return sum
    }

    override fun part2(input: String): Int {
        val grid = StandardGrid.build(input){BasicCell(it)}
        val gears = HashMap<Pair<Int, Int>, MutableList<Int>>()
        for (i in 0..input.lines().filter(String::isNotBlank).size) {
            var j = 0
            while (j <= input.lines().first().length) {
                if (grid[i, j]?.symbol?.isDigit() == true) {
                    val rangeStart = j
                    var (num, rangeEnd) = identifyNumber(grid, i, j)
                    j = rangeEnd
                    for ((k,cell) in grid.neighbors(i, (rangeStart..rangeEnd))) {
                        if(cell?.symbol == '*') {
                            val list = gears.computeIfAbsent(k){_ -> ArrayList()}
                            list.add(num)
                        }
                    }
                }
                j++
            }
        }
        return gears.entries.filter { (_, nums) -> nums.size == 2 }.sumOf { (_, nums) -> nums.reduce { a, n -> a * n } }
    }
}
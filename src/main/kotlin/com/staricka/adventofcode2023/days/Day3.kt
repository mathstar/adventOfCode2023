package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid

class Day3: Day {
    class Cell(override val symbol: Char): GridCell

    override fun part1(input: String): Int {
        var sum = 0
        val grid = StandardGrid.build(input){Cell(it)}
        for (i in 0..input.lines().filter(String::isNotBlank).size) {
            var j = 0
            while (j <= input.lines().first().length) {
                if (grid[i, j]?.symbol?.isDigit() == true) {
                    val rangeStart = j-1
                    var num = (grid[i, j]!!.symbol - '0')
                    while (grid[i, j+1]?.symbol?.isDigit() == true) {
                        num *= 10
                        num += grid[i, j+1]!!.symbol - '0'
                        j++
                    }
                    val rangeEnd = j+1
                    range@ for (ri in (i-1)..(i+1)) {
                        for (rj in rangeStart..rangeEnd) {
                            if(grid[ri, rj]?.symbol?.isDigit() == false && grid[ri, rj]?.symbol != null && grid[ri, rj]?.symbol != '.') {
                                sum += num
                                break@range
                            }
                        }
                    }
                }
                j++
            }
        }
        return sum
    }

    override fun part2(input: String): Int {
        val grid = StandardGrid.build(input){Cell(it)}
        val gears = HashMap<Pair<Int, Int>, MutableList<Int>>()
        for (i in 0..input.lines().filter(String::isNotBlank).size) {
            var j = 0
            while (j <= input.lines().first().length) {
                if (grid[i, j]?.symbol?.isDigit() == true) {
                    val rangeStart = j-1
                    var num = (grid[i, j]!!.symbol - '0')
                    while (grid[i, j+1]?.symbol?.isDigit() == true) {
                        num *= 10
                        num += grid[i, j+1]!!.symbol - '0'
                        j++
                    }
                    val rangeEnd = j+1
                    range@ for (ri in (i-1)..(i+1)) {
                        for (rj in rangeStart..rangeEnd) {
                            if(grid[ri, rj]?.symbol == '*') {
                                val list = gears.computeIfAbsent(Pair(ri,rj)){_ -> ArrayList()}
                                list.add(num)
                            }
                        }
                    }
                }
                j++
            }
        }
        return gears.entries.filter { (_, nums) -> nums.size == 2 }.sumOf { (_, nums) -> nums.reduce { a, n -> a * n } }
    }
}
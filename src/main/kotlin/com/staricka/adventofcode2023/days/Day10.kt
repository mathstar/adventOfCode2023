package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.BasicCell
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid

class Day10: Day {
    @Suppress("DANGEROUS_CHARACTERS")
    enum class PipeType(private val connectingVectors: List<Pair<Int, Int>>): GridCell {
        `|`(listOf(Pair(-1, 0), Pair(1, 0))),
        `-`(listOf(Pair(0, -1), Pair(0, 1))),
        L(listOf(Pair(-1, 0), Pair(0, 1))),
        J(listOf(Pair(-1, 0), Pair(0, -1))),
        `7`(listOf(Pair(0, -1), Pair(1, 0))),
        F(listOf(Pair(0, 1), Pair(1, 0))),
        S(emptyList());

        override val symbol: Char
            get() = this.name[0]

        fun connectsTo(source: Pair<Int, Int>, dest: Pair<Int, Int>): Boolean {
            return connectingVectors.map { Pair(it.first + source.first, it.second + source.second) }
                .any { it == dest }
        }

        companion object {
            fun determineType(source: Pair<Int, Int>, grid: Grid<PipeType>): PipeType {
                val up = listOf(`|`, `7`, F).contains(grid.get(source.plus(Pair(-1, 0))))
                val left = listOf(`-`, L, F).contains(grid.get(source.plus(Pair(0, -1))))
                val down = listOf(`|`, L, J).contains(grid.get(source.plus(Pair(1, 0))))
                val right = listOf(`-`, J, `7`).contains(grid.get(source.plus(Pair(0, 1))))

                return if (up && down) `|`
                else if (left && right) `-`
                else if (up && right) L
                else if (up && left) J
                else if (left && down) `7`
                else if (right && down) F
                else throw Exception("Failed to determine type")
            }
        }
    }

    private fun Grid<PipeType>.findStart(): Pair<Int, Int> {
        return this.cells().filter { (_,_,v) -> v == PipeType.S }.map { (x,y,_) -> Pair(x,y) }.first()
    }

    private fun findFarthest(grid: Grid<PipeType>, start: Pair<Int, Int>): Int {
        val visited = HashSet<Pair<Int, Int>>()
        var toProcess = setOf(start)
        var distance = -1

        while (toProcess.isNotEmpty()) {
            visited.addAll(toProcess)
            distance++
            val nextSet = HashSet<Pair<Int, Int>>()
            for (p in toProcess) {
                val (x,y) = p
                if (grid.get(p) == PipeType.S) {
                    nextSet.addAll(
                        grid.neighbors(x, y).filter {(neighbor, v) ->
                            v != null && v.connectsTo(neighbor, p)
                        }.map { it.key }
                    )
                } else {
                    nextSet.addAll(
                        grid.neighbors(x, y).filter { (neighbor, _) ->
                            grid.get(p)!!.connectsTo(p, neighbor)
                        }.map { it.key }
                    )
                }
            }
            nextSet.removeIf { visited.contains(it) }
            toProcess = nextSet
        }
        return distance
    }

    private fun getPath(grid: Grid<PipeType>, start: Pair<Int, Int>): HashSet<Pair<Int, Int>> {
        val visited = HashSet<Pair<Int, Int>>()
        var toProcess = setOf(start)

        while (toProcess.isNotEmpty()) {
            visited.addAll(toProcess)
            val nextSet = HashSet<Pair<Int, Int>>()
            for (p in toProcess) {
                val (x,y) = p
                if (grid.get(p) == PipeType.S) {
                    nextSet.addAll(
                        grid.neighbors(x, y).filter {(neighbor, v) ->
                            v != null && v.connectsTo(neighbor, p)
                        }.map { it.key }
                    )
                } else {
                    nextSet.addAll(
                        grid.neighbors(x, y).filter { (neighbor, _) ->
                            grid.get(p)?.connectsTo(p, neighbor) == true
                        }.map { it.key }
                    )
                }
            }
            nextSet.removeIf { visited.contains(it) }
            toProcess = nextSet
        }
        return visited
    }

    private fun doubleGrid(grid: Grid<PipeType>, path: Set<Pair<Int, Int>>): StandardGrid<PipeType> {
        val doubledGrid = StandardGrid<PipeType>()
        for ((ox,oy) in path) {
            val x = ox * 2
            val y = oy * 2
            doubledGrid[x,y] = grid[ox,oy]
            when(grid[ox,oy]) {
                PipeType.`|` -> doubledGrid[x+1,y] = PipeType.`|`
                PipeType.`-` -> doubledGrid[x,y+1] = PipeType.`-`
                PipeType.L -> doubledGrid[x,y+1] = PipeType.`-`
                PipeType.`7` -> doubledGrid[x+1,y] = PipeType.`|`
                PipeType.F -> {
                    doubledGrid[x+1,y] = PipeType.`|`
                    doubledGrid[x,y+1] = PipeType.`-`
                }
                else -> Unit
            }
        }
        return doubledGrid
    }

    private fun boundingBox(path: Set<Pair<Int, Int>>): Pair<Pair<Int, Int>, Pair<Int, Int>> =
        Pair(
            Pair(path.minOf { it.first } - 1, path.maxOf { it.first } + 1),
            Pair(path.minOf { it.second } - 1, path.maxOf { it.second } + 1)
        )

    private fun reachableFromOutside(path: Set<Pair<Int, Int>>, grid: Grid<PipeType>): Set<Pair<Int, Int>> {
        val (xBounds, yBounds) = boundingBox(path)
        val (minX, maxX) = xBounds
        val (minY, maxY) = yBounds

        val reachable = HashSet<Pair<Int, Int>>()
        var toProcess = HashSet<Pair<Int, Int>>()

        for (x in (minX..maxX)) {
            val a = Pair(x, minY)
            val b = Pair(x, maxY)
            if (!path.contains(a)) toProcess.add(a)
            if (!path.contains(b)) toProcess.add(b)
        }
        for (y in (minY..maxY)) {
            val a = Pair(minX, y)
            val b = Pair(maxX, y)
            if (!path.contains(a)) toProcess.add(a)
            if (!path.contains(b)) toProcess.add(b)
        }

        while (toProcess.isNotEmpty()) {
            reachable.addAll(toProcess)
            val nextSet = HashSet<Pair<Int, Int>>()
            for (p in toProcess) {
                val (x,y) = p
                nextSet.addAll(
                    grid.neighbors(x, y).filter { (neighbor, _) ->
                        val (nx, ny) = neighbor
                        nx in minX..maxX && ny in minY..maxY && !path.contains(neighbor) && !reachable.contains(neighbor)
                    }.map { it.key }
                )
            }
            toProcess = nextSet
        }
        return reachable
    }

    override fun part1(input: String): Int {
        val grid = StandardGrid.buildWithStrings(input, PipeType::valueOf)
        val start = grid.findStart()

        return findFarthest(grid, start)
    }

    override fun part2(input: String): Int {
        var grid = StandardGrid.buildWithStrings(input, PipeType::valueOf)
        val start = grid.findStart()
        var path = getPath(grid, start)
        grid[start.first, start.second] = PipeType.determineType(start, grid)

        grid = doubleGrid(grid, path)
        path = getPath(grid, Pair(start.first * 2, start.second * 2))

        val (xBounds, yBounds) = boundingBox(path)
        val (minX, maxX) = xBounds
        val (minY, maxY) = yBounds

        val reachableFromOutside = reachableFromOutside(path, grid)
        if (System.getenv("DEBUG") == "true") {
            println(toTroubleshootString(grid, path, reachableFromOutside))
        }

        return (minX..maxX).flatMap { x ->
            (minY..maxY).map { y -> Pair(x,y) }
        }.filter {
            !path.contains(it) && !reachableFromOutside.contains(it)
        }.count { (x,y) -> x % 2 == 0 && y % 2 == 0 }
    }

    private fun toTroubleshootString(grid: StandardGrid<PipeType>, path: Set<Pair<Int, Int>>, reachableFromOutside: Collection<Pair<Int, Int>>): String {
        val amendGrid = StandardGrid<GridCell>()
        val (xBounds, yBounds) = boundingBox(path)
        for (x in xBounds.first..xBounds.second) {
            for (y in yBounds.first..yBounds.second) {
                if (path.contains(Pair(x, y))) {
                    amendGrid[x, y] = BasicCell(grid[x,y]?.symbol ?: ' ')
                } else if (reachableFromOutside.contains(Pair(x, y))) {
                    amendGrid[x, y] = BasicCell('0')
                } else {
                    amendGrid[x, y] = BasicCell('■')
                }
            }
        }
        return amendGrid.pretty()
    }
}

fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)

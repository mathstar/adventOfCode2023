package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.BasicCell
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid
import java.lang.Exception

class Day10: Day {
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

    fun Grid<PipeType>.findStart(): Pair<Int, Int> {
        return this.cells().filter { (_,_,v) -> v == PipeType.S }.map { (x,y,_) -> Pair(x,y) }.first()
    }

    fun findFarthest(grid: Grid<PipeType>, start: Pair<Int, Int>): Int {
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

    fun getPath(grid: Grid<PipeType>, start: Pair<Int, Int>): HashSet<Pair<Int, Int>> {
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
                            grid.get(p)!!.connectsTo(p, neighbor)
                        }.map { it.key }
                    )
                }
            }
            nextSet.removeIf { visited.contains(it) }
            toProcess = nextSet
        }
        return visited
    }

    fun boundingBox(path: Set<Pair<Int, Int>>): Pair<Pair<Int, Int>, Pair<Int, Int>> =
        Pair(
            Pair(path.minOf { it.first } - 1, path.maxOf { it.first } + 1),
            Pair(path.minOf { it.second } - 1, path.maxOf { it.second } + 1)
        )

    fun reachableFromOutside(path: Set<Pair<Int, Int>>, grid: Grid<PipeType>): Set<Pair<Int, Int>> {
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
                        nx >= minX && nx <= maxX && ny >= minY && ny <= maxY && !path.contains(neighbor) && !reachable.contains(neighbor)
                    }.map { it.key }
                )

                for (crackDetection in crackDetectionDatas) {
                    if (crackDetection.applicable(p, grid, path)) {
                        val crackResult = crackDetection.flowThroughCrack(p, path, grid)
                        if (crackResult != null && !reachable.contains(crackResult)) {
                            nextSet.add(crackResult)
                        }
                    }
                }
            }
            toProcess = nextSet
        }
        return reachable
    }

    data class CrackDetectionData(
        val initializationVector1: Pair<Int, Int>,
        val initializationVector2: Pair<Int, Int>,
        val directionVector: Pair<Int, Int>,
        val acceptablePipeTypes1: List<PipeType>,
        val acceptablePipeTypes2: List<PipeType>
    ) {
        fun getBounds(initial: Pair<Int, Int>) = Pair(initial.plus(initializationVector1), initial.plus(initializationVector2))
        fun applicable(initial: Pair<Int, Int>, grid: Grid<PipeType>, path: Set<Pair<Int, Int>>): Boolean {
            val (crackBound1, crackBound2) = getBounds(initial)
            return path.contains(crackBound1)
                    && path.contains(crackBound2)
                    && acceptablePipeTypes1.contains(grid.get(crackBound1))
                    && acceptablePipeTypes2.contains(grid.get(crackBound2))
        }

        fun flowThroughCrack(
            initial: Pair<Int, Int>,
            path: Set<Pair<Int, Int>>,
            grid: Grid<PipeType>
        ): Pair<Int, Int>? {
            var crackBound1 = initial.plus(initializationVector1)
            var crackBound2 = initial.plus(initializationVector2)
            while (
                path.contains(crackBound1)
                && path.contains(crackBound2)
                && acceptablePipeTypes1.contains(grid.get(crackBound1))
                && acceptablePipeTypes2.contains(grid.get(crackBound2))
            ) {
                crackBound1 = crackBound1.plus(directionVector)
                crackBound2 = crackBound2.plus(directionVector)
            }
            return if (!path.contains(crackBound1)) {
                crackBound1
            } else if (!path.contains(crackBound2)) {
                crackBound2
            } else {
                null
            }
        }
    }
    val crackDetectionDatas = listOf(
        CrackDetectionData(Pair(-1, -1), Pair(-1,0), Pair(-1, 0), listOf(PipeType.J, PipeType.`7`, PipeType.`|`), listOf(PipeType.L, PipeType.F, PipeType.`|`)),
        CrackDetectionData(Pair(-1, 0), Pair(-1,1), Pair(-1, 0), listOf(PipeType.J, PipeType.`7`, PipeType.`|`), listOf(PipeType.L, PipeType.F, PipeType.`|`)),
        CrackDetectionData(Pair(1, -1), Pair(1,0), Pair(1, 0), listOf(PipeType.J, PipeType.`7`, PipeType.`|`), listOf(PipeType.L, PipeType.F, PipeType.`|`)),
        CrackDetectionData(Pair(1, 0), Pair(1,1), Pair(1, 0), listOf(PipeType.J, PipeType.`7`, PipeType.`|`), listOf(PipeType.L, PipeType.F, PipeType.`|`)),
        CrackDetectionData(Pair(-1, -1), Pair(0, -1), Pair(0, -1), listOf(PipeType.J, PipeType.L, PipeType.`-`), listOf(PipeType.`7`, PipeType.F, PipeType.`-`)),
        CrackDetectionData(Pair(0, -1), Pair(1, -1), Pair(0, -1), listOf(PipeType.J, PipeType.L, PipeType.`-`), listOf(PipeType.`7`, PipeType.F, PipeType.`-`)),
        CrackDetectionData(Pair(-1, 1), Pair(0, 1), Pair(0, 1), listOf(PipeType.J, PipeType.L, PipeType.`-`), listOf(PipeType.`7`, PipeType.F, PipeType.`-`)),
        CrackDetectionData(Pair(0, 1), Pair(1, 1), Pair(0, 1), listOf(PipeType.J, PipeType.L, PipeType.`-`), listOf(PipeType.`7`, PipeType.F, PipeType.`-`)),
    )

    override fun part1(input: String): Any? {
        val grid = StandardGrid.buildWithStrings(input, PipeType::valueOf)
        val start = grid.findStart()

        return findFarthest(grid, start)
    }

    override fun part2(input: String): Any? {
        val grid = StandardGrid.buildWithStrings(input, PipeType::valueOf)
        val start = grid.findStart()
        val path = getPath(grid, start)
        val (xBounds, yBounds) = boundingBox(path)
        val boundBoxArea = (xBounds.second - xBounds.first + 1)*(yBounds.second - yBounds.first + 1)

        grid[start.first, start.second] = PipeType.determineType(start, grid)
        val reachableFromOutside = reachableFromOutside(path, grid)

        println(toTroubleshootString(grid, path, reachableFromOutside))
        return boundBoxArea - path.size - reachableFromOutside.size
    }

    fun toTroubleshootString(grid: StandardGrid<PipeType>, path: Set<Pair<Int, Int>>, reachableFromOutside: Set<Pair<Int, Int>>): String {
        val ammendGrid = StandardGrid<GridCell>()
        val (xBounds, yBounds) = boundingBox(path)
        for (x in xBounds.first..xBounds.second) {
            for (y in yBounds.first..yBounds.second) {
                if (path.contains(Pair(x, y))) {
                    ammendGrid[x, y] = BasicCell(grid[x,y]!!.symbol)
                } else if (reachableFromOutside.contains(Pair(x, y))) {
                    ammendGrid[x, y] = BasicCell('0')
                } else {
                    ammendGrid[x, y] = BasicCell('■')
                }
            }
        }
        return ammendGrid.pretty()
    }
}

fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)

package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.days.CrucibleQueueKey.Companion.toQueueKey
import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.Direction
import com.staricka.adventofcode2023.util.Grid
import com.staricka.adventofcode2023.util.GridCell
import com.staricka.adventofcode2023.util.StandardGrid
import kotlin.math.min

data class CrucibleState(val heatLoss: Int, val direction: Direction, val directionStep: Int, val x: Int, val y: Int)

class CrucibleGridCell(val heatLoss: Int): GridCell {
    override val symbol = heatLoss.toString()[0]

    private val visited = HashMap<CrucibleQueueKey, Int>()
    private var minHeat: Int? = null

    fun visit(crucibleState: CrucibleState, minSteps: Int): Boolean {
        val crucibleHeatLoss = crucibleState.heatLoss

        val lowestHeatExisting = visited[crucibleState.toQueueKey()]
        if (lowestHeatExisting != null && lowestHeatExisting <= crucibleHeatLoss) {
            return false
        }
        visited[crucibleState.toQueueKey()] = crucibleHeatLoss

        if (minSteps <= crucibleState.directionStep) {
            minHeat = min(minHeat ?: Int.MAX_VALUE, crucibleHeatLoss)
        }
        return true
    }

    fun minVisitedHeat() = minHeat
}

fun CrucibleState.nextSteps(grid: Grid<CrucibleGridCell>, minSteps: Int = 0, maxSteps: Int = 3): List<CrucibleState> {
    if (x == grid.maxX && y == grid.maxY) {
        return emptyList()
    }

    val minEndHeat = grid[grid.maxX, grid.maxY]!!.minVisitedHeat()
    if (minEndHeat != null && minEndHeat <= heatLoss) {
        return emptyList()
    }

    return when (direction) {
        Direction.LEFT -> listOf(Pair(grid.left(x, y), Direction.LEFT), Pair(grid.up(x, y), Direction.UP), Pair(grid.down(x, y), Direction.DOWN))
        Direction.UP -> listOf(Pair(grid.up(x, y), Direction.UP), Pair(grid.left(x, y), Direction.LEFT), Pair(grid.right(x, y), Direction.RIGHT))
        Direction.RIGHT -> listOf(Pair(grid.right(x, y), Direction.RIGHT), Pair(grid.up(x, y), Direction.UP), Pair(grid.down(x, y), Direction.DOWN))
        Direction.DOWN -> listOf(Pair(grid.down(x, y), Direction.DOWN), Pair(grid.left(x, y), Direction.LEFT), Pair(grid.right(x, y), Direction.RIGHT))
    }.asSequence().filter { (dest, _) ->
        dest.third != null
    }.filter { (_, newDirection) ->
        directionStep >= minSteps || newDirection == direction
    }.map { (dest, newDirection) ->
        CrucibleState(heatLoss + grid[dest.first,dest.second]!!.heatLoss, newDirection, if (newDirection == direction) directionStep + 1 else 1, dest.first, dest.second)
    }.filter {
        it.directionStep <= maxSteps
    }.filter {
        grid[it.x, it.y]!!.visit(it, minSteps)
    }.toList()
}

data class CrucibleQueueKey(val direction: Direction, val directionStep: Int, val x: Int, val y: Int) {
    companion object {
        fun CrucibleState.toQueueKey() = CrucibleQueueKey(direction, directionStep, x, y)
    }
}

class CrucibleQueue {
    private val delegate = LinkedHashMap<CrucibleQueueKey, CrucibleState>()

    fun add(crucibleState: CrucibleState) {
        val key = crucibleState.toQueueKey()
        if(delegate.contains(key) && delegate[key]!!.heatLoss <= crucibleState.heatLoss) {
            return
        }
        delegate[key] = crucibleState
    }

    fun pop(): CrucibleState {
        val next = delegate.entries.first()
        delegate.remove(next.key)
        return next.value
    }

    fun isNotEmpty() = delegate.isNotEmpty()
}

fun Grid<CrucibleGridCell>.walk(minSteps: Int = 0, maxSteps: Int = 3): Int {
    val queue = CrucibleQueue()
    queue.add(CrucibleState(0, Direction.DOWN, 0, 0, 0))
    queue.add(CrucibleState(0, Direction.RIGHT, 0, 0, 0))

    while (queue.isNotEmpty()) {
        queue.pop().nextSteps(this, minSteps, maxSteps).forEach { queue.add(it) }
    }

    return this[maxX, maxY]!!.minVisitedHeat()!!
}

class Day17: Day {
    override fun part1(input: String): Int {
        return StandardGrid.build(input){CrucibleGridCell(it - '0')}.walk()
    }

    override fun part2(input: String): Int {
        return StandardGrid.build(input){CrucibleGridCell(it - '0')}.walk(minSteps = 4, maxSteps = 10)
    }
}
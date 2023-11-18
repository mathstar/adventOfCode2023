package com.staricka.adventofcode2023.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StandardGridEnumTest {
    @Test
    fun standardGridTest() {
        val grid = StandardGrid.buildWithStrings("A.\n.B", CellValue::valueOf)
        assertEquals(CellValue.A, grid[0,0])
        assertEquals(CellValue.B, grid[1,1])
        assertNull(grid[0,1])
        assertNull(grid[1,0])
        assertNull(grid[400, -500])

        assertEquals("A.\n.B", grid.pretty())

        grid[0,1] = CellValue.B
        assertEquals(CellValue.B, grid[0,1])

        grid.remove(0, 1)
        assertNull(grid[0,1])
    }

    enum class CellValue: GridCell {
        A, B;

        override val symbol: Char
            get() = name[0]
    }
}

class StandardGridDynamicTest {
    @Test
    fun standardGridTest() {
        val grid = StandardGrid<CellValue>()
        grid[3, 0] = CellValue(0, 'a')
        grid[-2, 3] = CellValue(1, 'b')
        assertEquals(CellValue(0, 'a'), grid[3,0])
        assertEquals(CellValue(1, 'b'), grid[-2,3])
        assertNull(grid[0,1])
        assertNull(grid[400, -500])

        assertEquals("...b\n....\n....\n....\n....\na...", grid.pretty())

        grid.remove(3, 0)
        assertNull(grid[3,0])

        assertEquals("b", grid.pretty())
    }

    data class CellValue(val value: Int, override val symbol: Char): GridCell
}
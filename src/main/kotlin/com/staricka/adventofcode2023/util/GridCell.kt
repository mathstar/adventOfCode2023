package com.staricka.adventofcode2023.util

interface GridCell {
    val symbol: Char
}

data class BasicCell(override val symbol: Char): GridCell
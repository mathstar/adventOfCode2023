package com.staricka.adventofcode2023.util

fun String.splitByBlankLines(): List<String> {
    val result = ArrayList<String>()
    val sb = StringBuilder()
    for (line in this.lines()) {
        if (line.isNotBlank()) {
            sb.appendLine(line)
        } else {
            result.add(sb.toString())
            sb.clear()
        }
    }
    if (sb.isNotBlank()) result.add(sb.toString())
    return result
}
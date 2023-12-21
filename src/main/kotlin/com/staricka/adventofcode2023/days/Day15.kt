package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day

class Hasher {
    var value = 0

    fun appendChar(c: Char) {
        value += c.code
        value *= 17
        value %= 256
    }

    companion object {
        fun hashString(string: String): Int {
            val hasher = Hasher()
            string.toCharArray().forEach { hasher.appendChar(it) }
            return hasher.value
        }
    }
}

class Boxes {
    private val delegate = Array<LinkedHashMap<String, Int>>(256){ LinkedHashMap() }

    operator fun set(key: String, focalLength: Int) {
        delegate[Hasher.hashString(key)][key] = focalLength
    }

    fun remove(key: String) {
        delegate[Hasher.hashString(key)].remove(key)
    }

    fun focusingPower(): Int {
        return delegate.withIndex().sumOf { (box, slots) ->
            slots.values.withIndex().sumOf { (slot, focalLength) -> (box + 1) * (slot + 1) * focalLength }
        }
    }
}

class Day15: Day {
    override fun part1(input: String): Int {
        return input.lines().joinToString(separator = "").split(",").sumOf { Hasher.hashString(it) }
    }

    override fun part2(input: String): Int {
        val boxes = Boxes()
        input.lines().joinToString(separator = "").split(",")
            .forEach {
                if (it.contains('=')) {
                    val split = it.split('=')
                    boxes[split[0]] = split[1].toInt()
                } else {
                    boxes.remove(it.split('-')[0])
                }
            }
        return boxes.focusingPower()
    }
}
package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day
import java.util.TreeMap
import kotlin.math.min

data class Mapping(val destinationStart:Long, val sourceStart:Long, val length:Long) {
    fun inRange(value:Long): Boolean = value >= sourceStart && value < sourceStart + length
    fun mapValue(value: Long): Long = destinationStart + (value - sourceStart)

    companion object {
        fun fromString(input: String): Mapping {
            val split = input.split(" ")
            return Mapping(split[0].toLong(), split[1].toLong(), split[2].toLong())
        }
    }
}

data class Range(val start: Long, val length: Long) {
    companion object {
        fun fromStartEnd(start: Long, end: Long): Range {
            return Range(start, end - start + 1)
        }
    }
}

class Mappings(val sourceType: String, val destinationType: String) {
    private val entries = TreeMap<Long, Mapping>()

    fun addMapping(mapping: Mapping) {
        entries[mapping.sourceStart] = mapping
    }

    fun getMapping(value: Long): Long {
        val potentialMatch = entries.floorEntry(value)
        if (potentialMatch == null || !potentialMatch.value.inRange(value)) return value
        return potentialMatch.value.mapValue(value)
    }

    fun getMappingForRange(input: Range): List<Range> {
        val result = ArrayList<Range>()
        var start = input.start
        while (start < input.start + input.length) {
            val floorMapping = entries.floorEntry(start)
            if (floorMapping != null && floorMapping.value.inRange(start)) {
                val end = min(
                    floorMapping.value.sourceStart + floorMapping.value.length - 1,
                    input.start + input.length - 1
                )
                result.add(Range.fromStartEnd(floorMapping.value.mapValue(start), floorMapping.value.mapValue(end)))
                start = end + 1
            } else {
                val ceilMapping = entries.ceilingEntry(start)
                val end =
                    if (ceilMapping != null) {
                        min(
                            input.start + input.length - 1,
                            ceilMapping.value.sourceStart - 1
                        )
                    } else {
                        input.start + input.length - 1
                    }
                result.add(Range.fromStartEnd(start, end))
                start = end + 1
            }
        }
        return result
    }

    companion object {
        fun fromString(input: String): Mappings {
            val split = input.split(" ")[0].split("-")
            return Mappings(split[0], split[2])
        }
    }
}

fun getLocationForSeed(seed: Long, mappings: Map<String, Mappings>): Long? {
    var value: Long? = seed
    var type: String? = "seed"

    while (type != "location" && value != null && type != null) {
        val relevantMapping = mappings[type]
        type = relevantMapping?.destinationType
        value = relevantMapping?.getMapping(value)
    }

    return value
}

fun getLocationForSeedRanges(seeds: List<Range>, mappings: Map<String, Mappings>): List<Range> {
    var values: List<Range> = seeds
    var type: String? = "seed"

    while (type != "location" && type != null) {
        val relevantMapping = mappings[type]
        type = relevantMapping?.destinationType
        values = values.flatMap { relevantMapping!!.getMappingForRange(it) }
    }

    return values
}

typealias Seeds = List<Long>
fun String.toSeeds(): Seeds {
    return this.split(":")[1].trim().split(" ").map { it.toLong() }
}

fun String.toSeedsFromRange(): List<Range> {
    val nums = this.toSeeds()
    var i = 0
    val result = ArrayList<Range>()
    while (i < nums.size) {
        result.add(Range(nums[i], nums[i+1]))
        i += 2
    }
    return result
}

class Day5: Day {
    override fun part1(input: String): Long {
        var seeds: Seeds? = null
        val mappings = HashMap<String, Mappings>()
        var currentMappings: Mappings? = null
        input.lines().forEach{
            if (seeds == null) {
                seeds = it.toSeeds()
            } else if (it.isBlank()) {
                currentMappings = null
            } else if (currentMappings == null) {
                currentMappings = Mappings.fromString(it)
                mappings[currentMappings!!.sourceType] = currentMappings!!
            } else {
                currentMappings!!.addMapping(Mapping.fromString(it))
            }
        }
        return seeds!!.mapNotNull { getLocationForSeed(it, mappings) }.min()
    }

    override fun part2(input: String): Long {
        var seeds: List<Range>? = null
        val mappings = HashMap<String, Mappings>()
        var currentMappings: Mappings? = null
        input.lines().forEach{
            if (seeds == null) {
                seeds = it.toSeedsFromRange()
            } else if (it.isBlank()) {
                currentMappings = null
            } else if (currentMappings == null) {
                currentMappings = Mappings.fromString(it)
                mappings[currentMappings!!.sourceType] = currentMappings!!
            } else {
                currentMappings!!.addMapping(Mapping.fromString(it))
            }
        }

        return getLocationForSeedRanges(seeds!!, mappings).minBy { it.start }.start
    }
}
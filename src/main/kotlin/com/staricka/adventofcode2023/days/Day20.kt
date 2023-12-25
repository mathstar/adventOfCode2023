package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.days.PulseModule.Companion.toPulseModule
import com.staricka.adventofcode2023.framework.Day
import java.util.LinkedList

interface PulseModule {
    val name: String
    val dest: List<String>
    fun inOriginalState(): Boolean
    fun processPulse(pulse: Pulse): List<Pulse>

    companion object {
        private val regex = Regex("""([%&]?)([a-z]+) -> ([a-z, ]+)""")

        fun String.toPulseModule(): PulseModule {
            val match = regex.find(this)!!
            val dest = match.groups[3]!!.value.split(", ")
            val name = match.groups[2]!!.value
            return when (match.groups[1]!!.value) {
                "" -> BroadcastModule(name, dest)
                "%" -> FlipFlopModule(name, dest)
                "&" -> ConjunctionModule(name, dest)
                else -> throw Exception()
            }
        }
    }
}

data class Pulse(val source: String, val dest: String, val high: Boolean)

class FlipFlopModule(override val name: String, override val dest: List<String>): PulseModule {
    var state = false

    override fun inOriginalState() = !state

    override fun processPulse(pulse: Pulse): List<Pulse> {
        if (pulse.high) return emptyList()

        state = !state
        return dest.map { Pulse(name, it, state) }
    }
}

class ConjunctionModule(override val name: String, override val dest: List<String>): PulseModule {
    var inputStates: MutableMap<String, Boolean> = mutableMapOf()

    fun updateInputs(inputs: List<PulseModule>) {
        inputStates = inputs.filter { it.dest.contains(name) }.map { it.name }.associateWith { false }.toMutableMap()
    }

    override fun inOriginalState() = inputStates.values.all { !it }

    override fun processPulse(pulse: Pulse): List<Pulse> {
        inputStates[pulse.source] = pulse.high
        if (inputStates.values.all { it }) {
            return dest.map { Pulse(name, it, false) }
        }
        return dest.map { Pulse(name, it, true) }
    }
}

class BroadcastModule(override val name: String, override val dest: List<String>): PulseModule {
    override fun inOriginalState() = true

    override fun processPulse(pulse: Pulse): List<Pulse> {
        return dest.map { Pulse(name, it, pulse.high) }
    }
}

class EndModule(override val name: String): PulseModule {
    override val dest = emptyList<String>()

    var signaled = false

    override fun inOriginalState() = true

    override fun processPulse(pulse: Pulse): List<Pulse> {
        if (!pulse.high) signaled = true
        return emptyList()
    }

}

data class LoopData(val length: Int, val lowPulses: Int, val highPulses: Int)

fun findLoop(modules: Map<String, PulseModule>, limit: Int?): LoopData {
    var count = 0
    var lowCount = 0
    var highCount = 0

    while (limit == null || count < limit) {
        count++

        val data = runIteration(modules)
        highCount += data.highPulses
        lowCount += data.lowPulses

        if (modules.values.all { it.inOriginalState() }) return LoopData(count, lowCount, highCount)
    }
    return LoopData(count, lowCount, highCount)
}

fun findExitSignal(modules: Map<String, PulseModule>): Int {
    var count = 0

    while (true) {
        count++

        val data = runIteration(modules)

        if ((modules["rx"] as EndModule).signaled) return count
    }
}

private fun runIteration(modules: Map<String, PulseModule>): LoopData {
    var highCount = 0
    var lowCount = 0

    val pulseQueue = LinkedList<Pulse>()
    pulseQueue += Pulse("button", "broadcaster", false)

    while (pulseQueue.isNotEmpty()) {
        val pulse = pulseQueue.pop()
        if (pulse.high) highCount++ else lowCount++
        pulseQueue.addAll(modules[pulse.dest]?.processPulse(pulse) ?: emptyList())
    }

    return LoopData(1, lowCount, highCount)
}

fun parseInput(input: String, andEndModule: Boolean = false): Map<String, PulseModule> {
    val modules = input.lines().filter { it.isNotBlank() }
        .map { it.toPulseModule() }
        .associateBy { it.name }
        .toMutableMap()
    if (andEndModule) modules["rx"] = EndModule("rx")
    modules.values.filterIsInstance<ConjunctionModule>()
        .forEach { it.updateInputs(modules.values.toList()) }
    return modules
}

class Day20: Day {
    override fun part1(input: String): Any? {
        val modules = parseInput(input)
        val loopData = findLoop(modules, 1000)

        val loops = 1000 / loopData.length
        val additionalIterations = 1000 % loopData.length

        var highPulses = loopData.highPulses.toLong() * loops
        var lowPulses = loopData.lowPulses.toLong() * loops

        for (i in 1..additionalIterations) {
            val iter = runIteration(modules)
            highPulses += iter.highPulses
            lowPulses += iter.lowPulses
        }

        return lowPulses * highPulses
    }

    override fun part2(input: String): Any? {
        val modules = parseInput(input, andEndModule = true)
        return findExitSignal(modules)
    }
}
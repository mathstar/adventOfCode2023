package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.days.PulseModule.Companion.toPulseModule
import com.staricka.adventofcode2023.framework.Day
import com.staricka.adventofcode2023.util.lcm
import java.util.LinkedList

interface PulseModule {
    val name: String
    val dest: List<String>
    var sources: List<String>

    fun updateSources(modules: List<PulseModule>) {
        sources = modules.filter { it.dest.contains(name) }.map { it.name }
    }

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
    override var sources = emptyList<String>()
    private var state = false

    override fun processPulse(pulse: Pulse): List<Pulse> {
        if (pulse.high) return emptyList()

        state = !state
        return dest.map { Pulse(name, it, state) }
    }
}

class ConjunctionModule(override val name: String, override val dest: List<String>): PulseModule {
    override var sources = emptyList<String>()
    private var inputStates: MutableMap<String, Boolean> = mutableMapOf()

    override fun updateSources(modules: List<PulseModule>) {
        super.updateSources(modules)
        updateInputs(modules)
    }

    private fun updateInputs(modules: List<PulseModule>) {
        inputStates = modules.filter { it.dest.contains(name) }.map { it.name }.associateWith { false }.toMutableMap()
    }

    override fun processPulse(pulse: Pulse): List<Pulse> {
        inputStates[pulse.source] = pulse.high
        if (inputStates.values.all { it }) {
            return dest.map { Pulse(name, it, false) }
        }
        return dest.map { Pulse(name, it, true) }
    }
}

class BroadcastModule(override val name: String, override val dest: List<String>): PulseModule {
    override var sources = emptyList<String>()
    override fun processPulse(pulse: Pulse): List<Pulse> {
        return dest.map { Pulse(name, it, pulse.high) }
    }
}

class EndModule(override val name: String): PulseModule {
    override var sources = emptyList<String>()
    override val dest = emptyList<String>()

    private var signaled = false

    override fun processPulse(pulse: Pulse): List<Pulse> {
        if (!pulse.high) signaled = true
        return emptyList()
    }

}

data class IterationData(val lowPulses: Int, val highPulses: Int)

fun findExitSignal(modules: Map<String, PulseModule>): Long {
    var count = 0

    // assume rx is fed by a single ConjunctionModule - throws exception otherwise
    val nodesFeedingOutput = (modules[modules["rx"]!!.sources.single()]!! as ConjunctionModule).sources
    val nodesFeedingOutputCycle = nodesFeedingOutput.associateWith<String, Int?> { null }.toMutableMap()

    while (true) {
        count++

        runIteration(modules, nodesFeedingOutputCycle, count)

        if (nodesFeedingOutputCycle.values.all { it != null }) return nodesFeedingOutputCycle.values.map { it!!.toLong() }.reduce{a,b -> lcm(a,b) }
    }
}

private fun runIteration(modules: Map<String, PulseModule>, nodesFeedingOutputCycle: MutableMap<String, Int?>? = null, iteration: Int = 0): IterationData {
    var highCount = 0
    var lowCount = 0

    val pulseQueue = LinkedList<Pulse>()
    pulseQueue += Pulse("button", "broadcaster", false)

    while (pulseQueue.isNotEmpty()) {
        val pulse = pulseQueue.pop()
        if (pulse.high) highCount++ else lowCount++
        if (pulse.high && nodesFeedingOutputCycle != null && nodesFeedingOutputCycle.containsKey(pulse.source) && nodesFeedingOutputCycle[pulse.source] == null) {
            nodesFeedingOutputCycle[pulse.source] = iteration
        }
        pulseQueue.addAll(modules[pulse.dest]?.processPulse(pulse) ?: emptyList())
    }

    return IterationData(lowCount, highCount)
}

fun parseInput(input: String, andEndModule: Boolean = false): Map<String, PulseModule> {
    val modules = input.lines().filter { it.isNotBlank() }
        .map { it.toPulseModule() }
        .associateBy { it.name }
        .toMutableMap()
    if (andEndModule) modules["rx"] = EndModule("rx")
    modules.values.forEach{ it.updateSources(modules.values.toList()) }
    return modules
}

class Day20: Day {
    override fun part1(input: String): Long {
        val modules = parseInput(input)

        var highPulses = 0L
        var lowPulses = 0L

        for (i in 1..1000) {
            val iter = runIteration(modules)
            highPulses += iter.highPulses
            lowPulses += iter.lowPulses
        }

        return lowPulses * highPulses
    }

    override fun part2(input: String): Long {
        val modules = parseInput(input, andEndModule = true)
        return findExitSignal(modules)
    }
}
package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.days.Part.Companion.toPart
import com.staricka.adventofcode2023.days.Workflow.Companion.toWorkflow
import com.staricka.adventofcode2023.days.WorkflowClause.Companion.toWorkflowClause
import com.staricka.adventofcode2023.days.WorkflowCondition.Companion.toWorkflowCondition
import com.staricka.adventofcode2023.days.WorkflowResult.Companion.toWorkflowResult
import com.staricka.adventofcode2023.framework.Day
import kotlin.math.max
import kotlin.math.min

data class Part(val extreme: Int, val musical: Int, val aerodynamic: Int, val shiny: Int) {
    val componentSum = extreme + musical + aerodynamic + shiny

    companion object {
        private val extremeRegex = Regex("x=([0-9]+)")
        private val musicalRegex = Regex("m=([0-9]+)")
        private val aerodynamicRegex = Regex("a=([0-9]+)")
        private val shinyRegex = Regex("s=([0-9]+)")

        fun String.toPart(): Part {
            return Part(
                extremeRegex.find(this)!!.groups[1]!!.value.toInt(),
                musicalRegex.find(this)!!.groups[1]!!.value.toInt(),
                aerodynamicRegex.find(this)!!.groups[1]!!.value.toInt(),
                shinyRegex.find(this)!!.groups[1]!!.value.toInt()
            )
        }
    }
}

data class ComponentRange(val extreme: IntRange, val musical: IntRange, val aerodynamic: IntRange, val shiny: IntRange) {
    val isEmpty = extreme.isEmpty() || musical.isEmpty() || aerodynamic.isEmpty() || shiny.isEmpty()
    val values: Long by lazy {
        extreme.count().toLong() * musical.count().toLong() * aerodynamic.count().toLong() * shiny.count().toLong()
    }

    fun overrideExtreme(incoming: IntRange) = ComponentRange(incoming, musical, aerodynamic, shiny)
    fun overrideMusical(incoming: IntRange) = ComponentRange(extreme, incoming, aerodynamic, shiny)
    fun overrideAerodynamic(incoming: IntRange) = ComponentRange(extreme, musical, incoming, shiny)
    fun overrideShiny(incoming: IntRange) = ComponentRange(extreme, musical, aerodynamic, incoming)
}

data class WorkflowCondition(val component: String, val comparison: String, val value: Int) {
    val componentFunction: (Part) -> Int = when (component) {
        "x" -> Part::extreme
        "m" -> Part::musical
        "a" -> Part::aerodynamic
        "s" -> Part::shiny
        else -> throw Exception()
    }

    val componentRangeFunction: (ComponentRange) -> IntRange = when (component) {
        "x" -> ComponentRange::extreme
        "m" -> ComponentRange::musical
        "a" -> ComponentRange::aerodynamic
        "s" -> ComponentRange::shiny
        else -> throw Exception()
    }

    val overrideFunction: (ComponentRange, IntRange) -> ComponentRange = when (component) {
        "x" -> ComponentRange::overrideExtreme
        "m" -> ComponentRange::overrideMusical
        "a" -> ComponentRange::overrideAerodynamic
        "s" -> ComponentRange::overrideShiny
        else -> throw Exception()
    }

    val comparisonFunction: (Int) -> Boolean = when (comparison) {
        "<" -> {a -> a < value}
        ">" -> {a -> a > value}
        else -> throw Exception()
    }

    fun applicable(part: Part): Boolean = comparisonFunction(componentFunction(part))

    fun applicableRange(inbound: ComponentRange): ComponentRange {
        val inboundRange = componentRangeFunction(inbound)
        val inboundStart = inboundRange.first
        val inboundEnd = inboundRange.last
        val overrideRange = when (comparison) {
            "<" -> inboundStart..min(inboundEnd, value - 1)
            ">" -> max(inboundStart, value + 1)..(inboundEnd)
            else -> throw Exception()
        }

        return overrideFunction(inbound, overrideRange)
    }

    fun nonApplicableRange(inbound: ComponentRange): ComponentRange {
        val inboundRange = componentRangeFunction(inbound)
        val inboundStart = inboundRange.first
        val inboundEnd = inboundRange.last
        val overrideRange = when (comparison) {
            "<" -> max(inboundStart, value)..inboundEnd
            ">" -> inboundStart..min(inboundEnd, value)
            else -> throw Exception()
        }

        return overrideFunction(inbound, overrideRange)
    }

    companion object {
        private val conditionRegex = Regex("([xmas])([<>])([0-9]+)")

        fun String.toWorkflowCondition(): WorkflowCondition {
            val match = conditionRegex.find(this)!!
            return WorkflowCondition(
                match.groups[1]!!.value,
                match.groups[2]!!.value,
                match.groups[3]!!.value.toInt()
            )
        }
    }
}

interface WorkflowResult {
    companion object {
        fun String.toWorkflowResult(): WorkflowResult {
            return if (this == "A") {
                FinalResult(true)
            } else if (this == "R") {
                FinalResult(false)
            } else {
                ForwardResult(this)
            }
        }
    }
}

data class ForwardResult(val destination: String): WorkflowResult
data class FinalResult(val accept: Boolean): WorkflowResult

data class WorkflowClause(val condition: WorkflowCondition?, val result: WorkflowResult) {
    fun process(part: Part) = if (condition?.applicable(part) != false) result else null

    fun positiveRange(inbound: ComponentRange): Pair<ComponentRange, WorkflowResult> {
        return if(condition == null) Pair(inbound, result) else Pair(condition.applicableRange(inbound), result)
    }

    fun negativeRange(inbound: ComponentRange): ComponentRange? {
        return condition?.nonApplicableRange(inbound)
    }

    companion object {
        fun String.toWorkflowClause(): WorkflowClause {
            val split = this.split(":")
            return if (split.size > 1) {
                WorkflowClause(split[0].toWorkflowCondition(), split[1].toWorkflowResult())
            } else {
                WorkflowClause(null, split[0].toWorkflowResult())
            }
        }
    }
}

data class Workflow(val name: String, val clauses: List<WorkflowClause>) {
    fun process(part: Part) = clauses.firstNotNullOf { it.process(part) }

    fun processRange(inbound: ComponentRange): Map<ComponentRange, WorkflowResult> {
        val resultRanges = HashMap<ComponentRange, WorkflowResult>()

        var toNext: ComponentRange? = inbound
        for (clause in clauses) {
            val (posRange, posResult) = clause.positiveRange(toNext!!)
            if (!posRange.isEmpty) {
                resultRanges[posRange] = posResult
            }

            toNext = clause.negativeRange(toNext)
        }

        return resultRanges
    }

    companion object {
        private val workflowRegex = Regex("([^{]+)\\{([^}]+)}")

        fun String.toWorkflow(): Workflow {
            val match = workflowRegex.find(this)!!
            return Workflow(
                match.groups[1]!!.value,
                match.groups[2]!!.value.split(",").map { it.toWorkflowClause() }
            )
        }
    }
}

fun processPart(part: Part, workflows: Map<String, Workflow>): FinalResult {
    var workflow = workflows["in"]!!
    while (true) {
        when(val result = workflow.process(part)) {
            is ForwardResult -> workflow = workflows[result.destination]!!
            is FinalResult -> return result
        }
    }
}

class Day19: Day {
    override fun part1(input: String): Int {
        val (workflows, parts) = parseInput(input)

        return parts.filter { processPart(it, workflows).accept }.sumOf { it.componentSum }
    }

    override fun part2(input: String): Long {
        val (workflows, _) = parseInput(input)

        val rangeResults = HashMap<ComponentRange, WorkflowResult>()
        rangeResults[ComponentRange(
            1..4000,
            1..4000,
            1..4000,
            1..4000
        )] = ForwardResult("in")

        while (true) {
            val forwards = rangeResults.filter { (_,v) -> v is ForwardResult }.mapValues { (_, v) ->  v as ForwardResult }
            if (forwards.isEmpty()) break

            for ((inbound, forwardResult) in forwards) {
                rangeResults.remove(inbound)
                rangeResults.putAll(workflows[forwardResult.destination]!!.processRange(inbound))
            }
        }

        return rangeResults.filter { (_,v) -> (v as FinalResult).accept }.map { (k,_) -> k.values }.sum()
    }

    private fun parseInput(input: String): Pair<HashMap<String, Workflow>, ArrayList<Part>> {
        val workflows = HashMap<String, Workflow>()
        val parts = ArrayList<Part>()

        var inWorkflows = true
        for (line in input.lines()) {
            if (line.isBlank()) {
                inWorkflows = false
            } else if (inWorkflows) {
                line.toWorkflow().let { workflows[it.name] = it }
            } else {
                parts += line.toPart()
            }
        }
        return Pair(workflows, parts)
    }
}
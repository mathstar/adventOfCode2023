package com.staricka.adventofcode2023.days

import com.staricka.adventofcode2023.framework.Day

class Day7: Day {
    enum class CardRank {
        A, K, Q, J, T, `9`, `8`, `7`, `6`, `5`, `4`, `3`, `2`, `1`
    }

    enum class CardRankWithJokers {
        A, K, Q, T, `9`, `8`, `7`, `6`, `5`, `4`, `3`, `2`, `1`, J
    }

    enum class HandType {
        FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD;

        companion object {
            fun determineType(hand: Hand): HandType {
                return determineType(hand.cards.groupBy { it }.map { it.value.size }.sortedDescending())
            }

            fun determineType(hand: HandWithJokers): HandType {
                val counts = hand.cards.groupBy { it }
                val jokerCount = counts[CardRankWithJokers.J]?.size ?: 0

                var sortedCounts = counts.filter { it.key != CardRankWithJokers.J }.map { it.value.size }.sortedDescending().toMutableList()
                if (sortedCounts.size > 0) sortedCounts[0] += jokerCount else sortedCounts = mutableListOf(jokerCount)

                return determineType(sortedCounts)
            }

            private fun determineType(sortedCounts: List<Int>): HandType {
                if (sortedCounts[0] == 5) return FIVE_OF_A_KIND
                if (sortedCounts[0] == 4) return FOUR_OF_A_KIND
                if (sortedCounts[0] == 3 && sortedCounts[1] == 2) return FULL_HOUSE
                if (sortedCounts[0] == 3) return THREE_OF_A_KIND
                if (sortedCounts[0] == 2 && sortedCounts[1] >= 2) return TWO_PAIR
                if (sortedCounts[0] == 2 ) return ONE_PAIR
                return HIGH_CARD
            }
        }
    }

    data class Hand(val cards: List<CardRank>, val bid: Int): Comparable<Hand> {
        override fun compareTo(other: Hand): Int {
            return compareBy<Hand>(
                {HandType.determineType(it)},
                {it.cards[0]},
                {it.cards[1]},
                {it.cards[2]},
                {it.cards[3]},
                {it.cards[4]},
            ).compare(this, other)
        }

        companion object {
            fun fromString(input: String): Hand {
                val split = input.split(" ")
                return Hand(
                    split[0].toCharArray().map { CardRank.valueOf(it.toString()) },
                    split[1].toInt()
                )
            }
        }
    }

    data class HandWithJokers(val cards: List<CardRankWithJokers>, val bid: Int): Comparable<HandWithJokers> {
        override fun compareTo(other: HandWithJokers): Int {
            return compareBy<HandWithJokers>(
                {HandType.determineType(it)},
                {it.cards[0]},
                {it.cards[1]},
                {it.cards[2]},
                {it.cards[3]},
                {it.cards[4]},
            ).compare(this, other)
        }

        override fun toString(): String {
            return cards.joinToString { it.name }
        }

        companion object {
            fun fromString(input: String): HandWithJokers {
                val split = input.split(" ")
                return HandWithJokers(
                    split[0].toCharArray().map { CardRankWithJokers.valueOf(it.toString()) },
                    split[1].toInt()
                )
            }
        }
    }

    override fun part1(input: String): Int {
        val hands = input.lines().filter { it.isNotBlank() }
            .map { Hand.fromString(it) }
            .sortedDescending()
        return hands.withIndex().sumOf { (i,hand) -> (i+1)*hand.bid }
    }

    override fun part2(input: String): Int {
        val hands = input.lines().filter { it.isNotBlank() }
            .map { HandWithJokers.fromString(it) }
            .sortedDescending()
        return hands.withIndex().sumOf { (i,hand) -> (i+1)*hand.bid }
    }
}
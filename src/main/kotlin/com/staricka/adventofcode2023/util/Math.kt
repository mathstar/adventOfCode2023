package com.staricka.adventofcode2023.util

fun lcm(n1: Long, n2: Long): Long {
    var gcd = 1
    var i = 1
    while (i <= n1 && i <= n2) {
        if (n1 % i == 0L && n2 % i == 0L) {
            gcd = i
        }
        i++
    }
    return n1 * n2 / gcd
}

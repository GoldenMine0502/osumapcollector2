package kr.goldenmine.util

import java.util.*

private val r: Random = Random()

fun getRandom(start: Int, finish: Int): Int {
    return r.nextInt(finish - start + 1) + start
}
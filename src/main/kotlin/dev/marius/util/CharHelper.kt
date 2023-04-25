package dev.marius.util

import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 27.11.2020, 16:24

*/
object CharHelper {
    private val CHARACTER_LIST: MutableList<Char> = ArrayList()
    @JvmStatic
    operator fun get(vararg ints: Int): String {
        val builder = StringBuilder()
        for (i in ints) builder.append(CHARACTER_LIST[i])
        return builder.toString()
    }

    init {
        for (c in "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVXYZäÄöÖüÜß0123456789".toCharArray()) CHARACTER_LIST.add(c)
    }
}
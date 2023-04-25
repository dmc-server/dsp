package dev.marius.util

import kotlin.collections.ArrayList

/*

Project: David Server Plugin
Author: Marius
Created: 27.11.2020, 16:07

*/
object IntegerUtil {
    private var INTEGER_CHARS: ArrayList<Char> = ArrayList()

    @JvmStatic
    fun instanceOfInt(string: String): Boolean {
        var res = true
        for (c in string.toCharArray()) if (!INTEGER_CHARS.contains(c)) {
            res = false
            break
        }
        return res
    }

    init {
        for (c in charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')) INTEGER_CHARS.add(c)
    }
}
package dev.marius.util

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 16:05

*/
object StringUtil {
    @JvmStatic
    fun join(array: Array<String>, format: String, separator: String): String {
        var out = StringBuilder()
        for (s in array) {
            out.append(format.replace("%s%".toRegex(), s)).append(separator).append(" ")
        }
        out = StringBuilder(out.substring(0, out.length - (separator.length + 1)))
        return out.toString()
    }
}
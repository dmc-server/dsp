package dev.marius.util

import java.text.SimpleDateFormat
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 15:32

*/
object DateUtil {
    @JvmStatic
    val currentDate: Date
        get() = object : Date {
            val millis = System.currentTimeMillis()
            override fun date(): String {
                return getFormat("dd.MM.yyyy").format(Date(millis))
            }

            override fun time(): String {
                return getFormat("HH:mm:ss").format(Date(millis))
            }

            override fun all(): String {
                return getFormat("dd.MM.yyyy HH:mm:ss").format(Date(millis))
            }

            override fun asJavaDate(): java.util.Date {
                return Date(millis)
            }
        }

    fun getFormat(pattern: String): SimpleDateFormat {
        val format = SimpleDateFormat(pattern)
        format.timeZone = TimeZone.getTimeZone("CET")
        return format
    }
}